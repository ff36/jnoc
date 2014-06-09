/*
 * Created May 19, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.model;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.app.service.internal.DefaultAttributeFilter;
import com.dastrax.app.services.AttributeFilter;
import com.dastrax.per.dap.CrudService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.criteria.CriteriaQuery;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * The data model that backs a data table in the presentation layer.
 *
 * @version 3.0-SNAPSHOT
 * @since Build 3.0.0 (May 19, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DataTable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(DataTable.class.getName());

    private List table;
    private Object[] selected;
    private DataTableModel model;
    private List filtered;
    private boolean render;
    private final ModelQuery modelQuery;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of DataTable.
     *
     * @param modelQuery
     */
    public DataTable(ModelQuery modelQuery) {
        this.modelQuery = modelQuery;
        this.table = new ArrayList<>();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of table. Table contains a List and is used for simple data
     * tables that have no DataTableModel.
     *
     * @return the value of table
     */
    public List getTable() {
        return table;
    }

    /**
     * Get the value of selected. When objects contained in either the 'table'
     * or 'model' are selected in the data table the selected objects are held
     * in the array.
     *
     * @return the value of selected
     */
    public Object[] getSelected() {
        return selected;
    }

    /**
     * Get the value of model. A data model used to construct and manipulate
     * data in a table.
     *
     * @return the value of model
     */
    public DataTableModel getModel() {
        return model;
    }

    /**
     * Get the value of filtered. Down size filtering an existing dataset does
     * not require repeat queries to the persistence layer. Instead the main
     * dataset integrity is maintained and the subset is held here.
     *
     * @return the value of filtered
     */
    public List getFiltered() {
        return filtered;
    }

    /**
     * Get the value of render. Simple value that can be used to determine when
     * the data is loaded and the table can be rendered.
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of table. Table contains a List and is used for simple data
     * tables that have no DataTableModel.
     *
     * @param table new value of table
     */
    public void setTable(List table) {
        this.table = table;
    }

    /**
     * Set the value of selected. When objects contained in either the 'table'
     * or 'model' are selected in the data table the selected objects are held
     * in the array.
     *
     * @param selected new value of selected
     */
    public void setSelected(Object[] selected) {
        this.selected = selected;
    }

    /**
     * Set the value of filtered. Down size filtering an existing dataset does
     * not require repeat queries to the persistence layer. Instead the main
     * dataset integrity is maintained and the subset is held here.
     *
     * @param filtered new value of filtered
     */
    public void setFiltered(List filtered) {
        this.filtered = filtered;
    }

//</editor-fold>
    
    /**
     * Table initializer. The amount of data to be loaded is dependent on the
     * callers parameters. Our inability to determine the amount of data before
     * hand means that a better user experience can be achieved by loading the
     * page first and subsequently loading the data by using an ajax remote
     * command call. Once the data has loaded we set the render property to
     * true.
     */
    public void initTable() {
        AttributeFilter attFilter = new DefaultAttributeFilter();

        // Optional filter map
        Map<String, List<String>> optionalMap = new HashMap<>();
        
        // Obtain all the query parameters
        Map<String, String> parameterMap = (Map<String, String>) FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();

        // Convert the parameters to a List<String, List<String>>
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            if (entry.getValue().contains(",")) {
                String[] value = entry.getValue().split(",");
                optionalMap.putIfAbsent(entry.getKey(), Arrays.asList(value));
            } else {
                List<String> single = new ArrayList(1);
                single.add(entry.getValue());
                optionalMap.putIfAbsent(entry.getKey(), single);
            }
        }

        this.model = new DataTableModel(
                this.modelQuery,
                attFilter.authorizedAuthors(),
                optionalMap);

        this.render = true;
    }

    /**
     * This class is dedicated to creating a PF LazyDataModel. Due to the nature
     * of lazy loading we want to dynamically create queries at runtime based on
     * specified filter conditions so that only the specified items are searched
     * and returned from the database. This increases the efficiency of the
     * search facility exponentially. The class accepts 3 filters that can be
     * optionally passed in. The root filter is the master filter applied by the
     * code base based on the specific subject to restrict the possible scope of
     * the search to only their authorized items. The optional filter is a
     * database filter that can be optionally passed in to create filter
     * presents that the subject can quickly access. The global filter is linked
     * to the search field in the table and will match the typed value against
     * the object attributes of the remaining values after the root and optional
     * filters have been applied.
     *
     * @version 3.0-SNAPSHOT
     * @since Build 2.0.0 (May 19, 2014)
     * @author Tarka L'Herpiniere
     * @author <tarka@solid.com>
     */
    public class DataTableModel extends LazyDataModel {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private final ModelQuery modelQuery;
        private final Map<String, List<String>> rootFilter;
        private final Map<String, List<String>> optionalFilter;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public DataTableModel(
                ModelQuery modelQuery,
                Map<String, List<String>> rootFilter,
                Map<String, List<String>> optionalFilter) {
            this.modelQuery = modelQuery;
            this.rootFilter = rootFilter;
            this.optionalFilter = optionalFilter;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Overrides">
        /**
         * The load method is called via ajax when ever a 'keyup' event occurs
         * for a data table GlobalFilter.
         *
         * @param first
         * @param pageSize
         * @param sortField
         * @param sortOrder
         * @param filters
         * @return A List of persistence records that match the CriteriaQuery
         */
        @Override
        public List load(
                int first,
                int pageSize,
                String sortField,
                SortOrder sortOrder,
                Map filters) {

            try {
                CrudService dap = (CrudService) InitialContext.doLookup(
                        ResourceBundle.getBundle("config").getString("CRUD"));

                CriteriaQuery query = modelQuery.query(
                        first,
                        pageSize,
                        sortField,
                        sortOrder,
                        dap.getCriteriaBuilder(),
                        filters,
                        rootFilter,
                        optionalFilter);

                // Get the full query row count
                int rowCount = dap.findWithCriteriaQuery(query).size();

                // Paginate the query
                List data = dap.findWithCriteriaQuery(query, first, pageSize);

                setRowCount(rowCount);

                return data;
            } catch (NamingException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            return new ArrayList();
        }
//</editor-fold>

    }
}
