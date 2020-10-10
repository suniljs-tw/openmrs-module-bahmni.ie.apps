package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.field;

import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONObject;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.*;

public class NumericField implements IField {

    @Override
    public void addField(BahmniPDFForm bahmniPDFForm, JSONObject control) {
        String label = (String) ((JSONObject) control.get(CONCEPT)).get(NAME);
        JSONObject controlLabel = (JSONObject) control.get(LABEL);
        String unit = (String) controlLabel.get(UNITS);
        bahmniPDFForm.addNumericField(label, unit);
    }
}
