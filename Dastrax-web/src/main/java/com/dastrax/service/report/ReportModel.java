/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.report;

import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @version Build 2.0.0
 * @since Sep 11, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class ReportModel extends ListDataModel<Report> implements SelectableDataModel<Report> {    
  
    public ReportModel() {  
    }  
  
    public ReportModel(List<Report> data) {  
        super(data);  
    }  
      
    @Override  
    public Report getRowData(String rowKey) {    
        List<Report> reports = (List<Report>) getWrappedData();  
          
        for(Report report : reports) {  
            if(report.getId().equals(rowKey))  
                return report;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Report report) {  
        return report.getId();  
    }  
}  
