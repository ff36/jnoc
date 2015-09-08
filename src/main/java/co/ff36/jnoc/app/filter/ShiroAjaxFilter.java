/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package co.ff36.jnoc.app.filter;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.web.filter.authc.UserFilter;

/**
 * SHIRO needs this extra filter to correctly handle AJAX responses.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 15, 2013)
 * @author Tarka L'Herpiniere

 */
public class ShiroAjaxFilter extends UserFilter {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final String FACES_REDIRECT_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<partial-response><redirect url=\"%s\">"
            + "</redirect></partial-response>";
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    protected void redirectToLogin(
            ServletRequest request,
            ServletResponse response) throws IOException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        if ("partial/ajax".equals(httpRequest.getHeader("Faces-Request"))) {
            response.setContentType("text/xml");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().printf(
                    FACES_REDIRECT_XML,
                    httpRequest.getContextPath() + getLoginUrl());
        }
        else {
            super.redirectToLogin(request, response);
        }
    }
//</editor-fold>

}
