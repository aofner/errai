/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.enterprise.jaxrs.client.shared.interceptor;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.enterprise.client.jaxrs.api.interceptor.RestCallContext;
import org.jboss.errai.enterprise.client.jaxrs.api.interceptor.RestClientInterceptor;

/**
 * Rest client interceptor for testing chained interceptors.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class RestCallInterceptorOne implements RestClientInterceptor {

  @Override
  public void aroundInvoke(final RestCallContext context) {
    String parm = (String) context.getParameters()[0];
    parm += "A";
    context.setParameters(new Object[] {parm});
    context.proceed(new RemoteCallback<String>() {
      @Override
      public void callback(String response) {
        context.setResult(response += "D");
      }
    });
  }
}
