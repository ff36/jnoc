/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.model;

import com.dastrax.per.entity.core.Company;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @version Build 2.0.0
 * @since Jul 26, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class CompanyModel extends ListDataModel<Company> implements SelectableDataModel<Company> {    
  
    public CompanyModel() {  
    }  
  
    public CompanyModel(List<Company> data) {  
        super(data);  
    }  
      
    @Override  
    public Company getRowData(String rowKey) {  
        //Could use a more efficient way like a query by rowKey should be implemented to deal with huge data   
        List<Company> companies = (List<Company>) getWrappedData();  
          
        for(Company company : companies) {  
            if(company.getId().equals(rowKey)) {
                return company;
            }  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Company company) {  
        return company.getId();  
    }

}
