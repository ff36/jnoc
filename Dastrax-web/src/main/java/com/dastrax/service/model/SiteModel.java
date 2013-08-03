/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.model;

import com.dastrax.per.entity.core.Site;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @version Build 2.0.0
 * @since Jul 22, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class SiteModel extends ListDataModel<Site> implements SelectableDataModel<Site> {    
  
    public SiteModel() {  
    }  
  
    public SiteModel(List<Site> data) {  
        super(data);  
    }  
      
    @Override  
    public Site getRowData(String rowKey) {  
        //Could use a more efficient way like a query by rowKey should be implemented to deal with huge data   
        List<Site> sites = (List<Site>) getWrappedData();  
          
        for(Site site : sites) {  
            if(site.getId().equals(rowKey)) {
                return site;
            }  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Site site) {  
        return site.getId();  
    }  

}
