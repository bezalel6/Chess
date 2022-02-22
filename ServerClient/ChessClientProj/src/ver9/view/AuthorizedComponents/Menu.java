/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ver9.view.AuthorizedComponents;

import ver9.SharedClasses.AuthSettings;

import javax.swing.*;

public class Menu extends JMenu implements AuthorizedComponent {
    final @AuthSettings
    int authSettings;

    public Menu(String s, @AuthSettings int authSettings) {
        super(s);
        this.authSettings = authSettings;
        setAuth(null);
    }

    @Override
    public int authSettings() {
        return authSettings;
    }
}
