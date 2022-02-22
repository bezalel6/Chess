/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ver9.view.AuthorizedComponents;

import ver9.SharedClasses.DBActions.RequestBuilder;
import ver9.SharedClasses.StrUtils;

public class RequestMenuItem extends MenuItem implements AuthorizedComponent {
    public RequestMenuItem(RequestBuilder.PreMadeRequest request) {
        super(StrUtils.format(request.name()), request.authSettings);
    }
}
