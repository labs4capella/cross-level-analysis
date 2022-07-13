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

import com.navalgroup.capella.permission.extension.IPermission;
import com.navalgroup.capella.permission.extension.PermissionExtensionRegistry;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;

public final class PermissionRegistryUtil {
  public static LockedInstanceException lockSession(Session session) {
    return lockSession(session, null);
  }
  
  public static LockedInstanceException lockSession(Session session, String key) {
    IPermission permissionExtension = PermissionExtensionRegistry.getHighestPriorityPermission(key);
    if (permissionExtension != null)
      return permissionExtension.lockSessionModels(session); 
    return null;
  }
  
  public static LockedInstanceException lockElement(EObject element) {
    return lockElement(element, null);
  }
  
  public static LockedInstanceException lockElement(EObject element, String key) {
    IPermission permissionExtension = PermissionExtensionRegistry.getHighestPriorityPermission(key);
    if (permissionExtension != null)
      return permissionExtension.lockElement(element); 
    return null;
  }
  
  public static boolean unlockSession(Session session) {
    return unlockSession(session, null);
  }
  
  public static boolean unlockSession(Session session, String key) {
    IPermission permissionExtension = PermissionExtensionRegistry.getHighestPriorityPermission(key);
    if (permissionExtension != null) {
      boolean sessionLocked = permissionExtension.unlockSessionModels(session);
      if (!sessionLocked)
        return false; 
    } 
    return true;
  }
  
  public static boolean unlockElement(EObject element) {
    return unlockElement(element, null);
  }
  
  public static boolean unlockElement(EObject element, String key) {
    IPermission permissionExtension = PermissionExtensionRegistry.getHighestPriorityPermission(key);
    if (permissionExtension != null) {
      boolean sessionLocked = permissionExtension.unlockElement(element);
      if (!sessionLocked)
        return false; 
    } 
    return true;
  }
}
