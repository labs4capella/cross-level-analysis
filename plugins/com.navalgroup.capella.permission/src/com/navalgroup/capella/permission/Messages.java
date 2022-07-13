/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.permission;

import org.eclipse.sirius.ext.base.I18N;
import org.eclipse.sirius.ext.base.I18N.TranslatableMessage;

public class Messages {
  @TranslatableMessage
  public static String PermissionUtil_UnlockSession_UnlockFailed;
  
  @TranslatableMessage
  public static String PermissionUtil_LockSession_WhoLockWhat1;
  
  @TranslatableMessage
  public static String PermissionUtil_LockSession_WhoLockWhat2;
  
  @TranslatableMessage
  public static String PermissionUtil_LockSession_WhoLockWhat3;
  
  @TranslatableMessage
  public static String PermissionUtil_UnlockElement_UnlockFailed;
  
  @TranslatableMessage
  public static String PermissionUtil_LockElement_WhoLockWhat;
  
  static {
    I18N.initializeMessages(Messages.class, PermissionPlugin.INSTANCE);
  }
}
