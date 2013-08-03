/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.model;

import com.dastrax.per.entity.core.Subject;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @version Build 2.0.0
 * @since Jul 22, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class SubjectModel extends ListDataModel<Subject> implements SelectableDataModel<Subject> {    
  
    public SubjectModel() {  
    }  
  
    public SubjectModel(List<Subject> data) {  
        super(data);  
    }  
      
    @Override  
    public Subject getRowData(String rowKey) {  
        //Could use a more efficient way like a query by rowKey should be implemented to deal with huge data   
        List<Subject> subjects = (List<Subject>) getWrappedData();  
          
        for(Subject subject : subjects) {  
            if(subject.getUid().equals(rowKey)) {
                return subject;
            }  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Subject subject) {  
        return subject.getUid();  
    }  

}
