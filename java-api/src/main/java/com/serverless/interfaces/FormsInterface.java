package com.serverless.interfaces;

import java.util.List;

import com.serverless.models.Form;

public interface FormsInterface {
    
    List<Form> findAllForms();

    void saveForm(Form form);

    Form getForm(String PK);

    Form getFormByTitle(String title);
    
    void updateForm(Form form);

    void deleteForm(Form form);
}
