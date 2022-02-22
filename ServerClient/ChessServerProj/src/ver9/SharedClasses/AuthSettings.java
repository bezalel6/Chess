/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ver9.SharedClasses;

import org.intellij.lang.annotations.MagicConstant;

@MagicConstant(intValues = {AuthSettings.NEVER_AUTH, AuthSettings.GUEST, AuthSettings.USER, AuthSettings.ANY_LOGIN})
public @interface AuthSettings {
    int GUEST = 1, USER = 2;
    int ANY_LOGIN = GUEST | USER;
    int NEVER_AUTH = 4;
}
