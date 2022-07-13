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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class PermissionExtensionDescriptor {
  public static final String CLASS_ATTRIBUTE = "class";
  
  public static final String PRIORITY_ATTRIBUTE = "priority";
  
  public static final String KEY_ATTRIBUTE = "key";
  
  private final IConfigurationElement element;
  
  private final String extensionClassName;
  
  private final String priority;
  
  private final String key;
  
  private IPermission extension;
  
  public PermissionExtensionDescriptor(IConfigurationElement configuration) {
    this.element = configuration;
    this.extensionClassName = configuration.getAttribute("class");
    this.priority = configuration.getAttribute("priority");
    this.key = configuration.getAttribute("key");
  }
  
  public String getKey() {
    return this.key;
  }
  
  public String getExtensionClassName() {
    return this.extensionClassName;
  }
  
  public Integer getPriority() {
    Integer priorityValue;
    try {
      priorityValue = Integer.valueOf(this.priority);
    } catch (NumberFormatException numberFormatException) {
      priorityValue = Integer.valueOf(-1);
    } 
    return priorityValue;
  }
  
  public IPermission getPermission() {
    if (this.extension == null)
      try {
        this.extension = (IPermission)this.element.createExecutableExtension("class");
      } catch (CoreException coreException) {} 
    return this.extension;
  }
}
