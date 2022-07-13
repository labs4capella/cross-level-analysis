/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.permission.extension;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;

public interface IPermission {
  LockedInstanceException lockElement(EObject paramEObject);
  
  boolean unlockElement(EObject paramEObject);
  
  LockedInstanceException lockSessionModels(Session paramSession);
  
  boolean unlockSessionModels(Session paramSession);
  
  String getLockedObjects(LockedInstanceException paramLockedInstanceException);
  
  EObject getCapellaRootObject(Session paramSession);
  
  String getConnectedUsers(Session paramSession);
}
