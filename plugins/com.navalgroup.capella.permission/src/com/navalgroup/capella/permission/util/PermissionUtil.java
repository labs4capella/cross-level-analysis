/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.permission.util;

import com.navalgroup.capella.permission.Messages;
import com.navalgroup.capella.permission.extension.IPermission;
import com.navalgroup.capella.permission.extension.PermissionExtensionRegistry;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;

public final class PermissionUtil {
  public static final String PERMISSION_DEFAULT_KEY = "capella.permission.default";
  
  public static String getLockSessionErrorMessage(Session session, LockedInstanceException lie) {
    return getLockSessionErrorMessage(session, lie, null);
  }
  
  public static String getLockSessionErrorMessage(Session session, LockedInstanceException lie, String key) {
    StringBuilder whoLocksWhat = new StringBuilder();
    IPermission permissionExtension = PermissionExtensionRegistry.getHighestPriorityPermission(key);
    if (permissionExtension != null) {
      String lockedObjects = permissionExtension.getLockedObjects(lie);
      String connectedUsers = permissionExtension.getConnectedUsers(session);
      whoLocksWhat.append(Messages.PermissionUtil_LockSession_WhoLockWhat1);
      whoLocksWhat.append(System.lineSeparator());
      whoLocksWhat.append(System.lineSeparator());
      whoLocksWhat.append(Messages.PermissionUtil_LockSession_WhoLockWhat2);
      whoLocksWhat.append(System.lineSeparator());
      whoLocksWhat.append(lockedObjects);
      whoLocksWhat.append(System.lineSeparator());
      whoLocksWhat.append(Messages.PermissionUtil_LockSession_WhoLockWhat3);
      whoLocksWhat.append(System.lineSeparator());
      whoLocksWhat.append(connectedUsers);
    } 
    return whoLocksWhat.toString();
  }
  
  public static String getLockElementErrorMessage(Session session, LockedInstanceException lockedInstanceException) {
    return getLockElementErrorMessage(session, lockedInstanceException, null);
  }
  
  public static String getLockElementErrorMessage(Session session, LockedInstanceException lockedInstanceException, String key) {
    String elementName = "";
    String connectedUsers = "";
    elementName = getDisplayName(lockedInstanceException.getLockedElement());
    IPermission permissionExtension = PermissionExtensionRegistry.getHighestPriorityPermission(key);
    if (permissionExtension != null)
      connectedUsers = permissionExtension.getConnectedUsers(session); 
    return String.format(Messages.PermissionUtil_LockElement_WhoLockWhat, new Object[] { elementName, connectedUsers });
  }
  
  public static String getDisplayName(EObject element) {
    String dispayName = "";
    if (element != null) {
      EStructuralFeature nameFeature = element.eClass().getEStructuralFeature("name");
      if (nameFeature != null) {
        Object eGet = element.eGet(nameFeature);
        if (eGet instanceof String) {
          dispayName = (String)eGet;
        } else {
          dispayName = element.eClass().getName();
        } 
      } else {
        dispayName = element.eClass().getName();
      } 
    } 
    return dispayName;
  }
}
