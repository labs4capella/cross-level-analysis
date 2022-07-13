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

import com.navalgroup.capella.permission.extension.IPermission;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;

public class DefaultPermission implements IPermission {
  public LockedInstanceException lockSessionModels(Session session) {
    return null;
  }
  
  public boolean unlockSessionModels(Session session) {
    return true;
  }
  
  public String getLockedObjects(LockedInstanceException lie) {
    return null;
  }
  
  public String getConnectedUsers(Session session) {
    return "";
  }
  
  public LockedInstanceException lockElement(EObject element) {
    return null;
  }
  
  public boolean unlockElement(EObject element) {
    return true;
  }
  
  public EObject getCapellaRootObject(Session session) {
    return null;
  }
}
