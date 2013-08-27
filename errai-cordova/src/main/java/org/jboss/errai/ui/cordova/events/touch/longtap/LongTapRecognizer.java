package org.jboss.errai.ui.cordova.events.touch.longtap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HasHandlers;
import org.jboss.errai.ui.cordova.events.touch.AbstractRecognizer;
import org.jboss.errai.ui.cordova.events.touch.GwtTimerExecutor;
import org.jboss.errai.ui.cordova.events.touch.TimerExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author edewit@redhat.com
 */
public class LongTapRecognizer extends AbstractRecognizer {

  public static final int DEFAULT_WAIT_TIME_IN_MS = 1500;
  public static final int DEFAULT_MAX_DISTANCE = 15;

  private final HasHandlers source;
  private State state = State.READY;
  private TimerExecutor timerExecutor;
  private final int numberOfFingers;
  private List<Touch> startPositions;
  private int touchCount;

  public LongTapRecognizer(HasHandlers source) {
    this(source, new GwtTimerExecutor(), 1);
  }

  public LongTapRecognizer(HasHandlers source, int numberOfFingers) {
    this(source, new GwtTimerExecutor(), numberOfFingers);
  }

  protected LongTapRecognizer(HasHandlers source, TimerExecutor timerExecutor, int numberOfFingers) {
    this.source = source;
    this.timerExecutor = timerExecutor;
    this.numberOfFingers = numberOfFingers;
    this.startPositions = new ArrayList<Touch>();
  }

  @Override
  public void onTouchStart(final TouchStartEvent event) {
    JsArray<Touch> touches = event.getTouches();
    touchCount++;

    switch (state) {
      case INVALID:
        break;
      case READY:
        startPositions.add(touches.get(touchCount - 1));
        state = State.FINGERS_DOWN;
        break;
      case FINGERS_DOWN:
        startPositions.add(touches.get(touchCount - 1));
        break;
      case FINGERS_UP:
      default:
        state = State.INVALID;
        break;
    }

    if (touchCount == numberOfFingers) {
      state = State.WAITING;
      timerExecutor.execute(new TimerExecutor.CodeToRun() {

        @Override
        public void onExecution() {
          if (state != State.WAITING) {
            // something else happened forget it
            return;
          }

          source.fireEvent(new LongTapEvent(source, numberOfFingers, DEFAULT_WAIT_TIME_IN_MS, startPositions));
          reset();

        }
      }, DEFAULT_WAIT_TIME_IN_MS);
    }

    if (touchCount > numberOfFingers) {
      state = State.INVALID;
    }
  }

  @Override
  public void onTouchMove(TouchMoveEvent event) {
    switch (state) {
      case WAITING:
      case FINGERS_DOWN:
      case FINGERS_UP:
        // compare positions
        JsArray<Touch> currentTouches = event.getTouches();
        for (int i = 0; i < currentTouches.length(); i++) {
          Touch currentTouch = currentTouches.get(i);
          for (Touch startTouch : startPositions) {
            if (currentTouch.getIdentifier() == startTouch.getIdentifier()) {
              if (Math.abs(currentTouch.getPageX() - startTouch.getPageX()) > DEFAULT_MAX_DISTANCE
                      || Math.abs(currentTouch.getPageY() - startTouch.getPageY()) > DEFAULT_MAX_DISTANCE) {
                state = State.INVALID;
                break;
              }
            }
            if (state == State.INVALID) {
              break;
            }
          }
        }

        break;

      default:
        state = State.INVALID;
        break;
    }
  }

  @Override
  public void onTouchEnd(TouchEndEvent event) {
    int currentTouches = event.getTouches().length();
    switch (state) {
      case WAITING:
        state = State.INVALID;
        break;

      case FINGERS_DOWN:
        state = State.FINGERS_UP;
        break;
      case FINGERS_UP:
        // are we ready?
        if (currentTouches == 0 && touchCount == numberOfFingers) {
          // fire and reset

          reset();
        }
        break;

      case INVALID:
      default:
        if (currentTouches == 0)
          reset();
        break;
    }
  }

  @Override
  public void onTouchCancel(TouchCancelEvent event) {
    state = State.INVALID;
    int currentTouches = event.getTouches().length();
    if (currentTouches == 0)
      reset();
  }

  protected void reset() {
    state = State.READY;
    touchCount = 0;
  }
}