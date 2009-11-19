package org.jboss.errai.widgets.rebind.widgetmappers;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.widgets.rebind.FieldMapperGenerator;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextBoxFMGenerator implements FieldMapperGenerator {
    public String generateFieldMapperGenerator(TypeOracle oracle, JField targetWidget,
                                               JType targetType, JField targetFieldType, String fieldName) {
        InputStream istream = this.getClass().getResourceAsStream("TextBoxFMGenerator.mv");
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("typeOracle", oracle);
        vars.put("targetWidget", targetWidget.getType().isClassOrInterface().getQualifiedSourceName());
        vars.put("targetType", targetType.isClassOrInterface().getQualifiedSourceName());
        vars.put("fieldType", targetFieldType.getType().isClassOrInterface().getQualifiedSourceName());
        vars.put("fieldName", fieldName);

        return (String) TemplateRuntime.eval(istream, null, new MapVariableResolverFactory(vars), null);
    }

    public String generateValueExtractorStatement(TypeOracle oracle, JField targetWidget,
                                                  JType targetType, JField targetFieldType, String fieldName) {
        return "getText()";
    }

    public String init(TypeOracle oracle, JField targetWidget, JType targetType, String variable, List<JField> fields) {
        return "";
    }
}
