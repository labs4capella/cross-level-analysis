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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;

public class PermissionRegistryListener implements IRegistryEventListener {
  public static final String PERMISSION_EXTENSION_POINT = "com.navalgroup.capella.permission";
  
  private static final String PERMISSION_TAG_EXTENSION = "permission";
  
  public void init() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    registry.addListener(this, "com.navalgroup.capella.permission");
    parseInitialContributions();
  }
  
  public void dispose() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    registry.removeListener(this);
    PermissionExtensionRegistry.clearRegistry();
  }
  
  public void added(IExtensionPoint[] extensionPoints) {}
  
  public void removed(IExtensionPoint[] extensionPoints) {}
  
  public void added(IExtension[] extensions) {
    byte b;
    int i;
    IExtension[] arrayOfIExtension;
    for (i = (arrayOfIExtension = extensions).length, b = 0; b < i; ) {
      IExtension extension = arrayOfIExtension[b];
      parseExtension(extension);
      b++;
    } 
  }
  
  public void parseInitialContributions() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    byte b;
    int i;
    IExtension[] arrayOfIExtension;
    for (i = (arrayOfIExtension = registry.getExtensionPoint("com.navalgroup.capella.permission").getExtensions()).length, b = 0; b < i; ) {
      IExtension extension = arrayOfIExtension[b];
      parseExtension(extension);
      b++;
    } 
  }
  
  public void removed(IExtension[] extensions) {
    byte b;
    int i;
    IExtension[] arrayOfIExtension;
    for (i = (arrayOfIExtension = extensions).length, b = 0; b < i; ) {
      IExtension extension = arrayOfIExtension[b];
      IConfigurationElement[] configElements = extension.getConfigurationElements();
      byte b1;
      int j;
      IConfigurationElement[] arrayOfIConfigurationElement1;
      for (j = (arrayOfIConfigurationElement1 = configElements).length, b1 = 0; b1 < j; ) {
        IConfigurationElement elem = arrayOfIConfigurationElement1[b1];
        if ("permission".equals(elem.getName())) {
          String extensionClassName = elem.getAttribute("class");
          PermissionExtensionRegistry.removeExtension(extensionClassName);
        } 
        b1++;
      } 
      b++;
    } 
  }
  
  private void parseExtension(IExtension extension) {
    IConfigurationElement[] configElements = extension.getConfigurationElements();
    byte b;
    int i;
    IConfigurationElement[] arrayOfIConfigurationElement1;
    for (i = (arrayOfIConfigurationElement1 = configElements).length, b = 0; b < i; ) {
      IConfigurationElement elem = arrayOfIConfigurationElement1[b];
      if ("permission".equals(elem.getName()))
        try {
          PermissionExtensionRegistry.addExtension(new PermissionExtensionDescriptor(elem));
        } catch (IllegalArgumentException illegalArgumentException) {} 
      b++;
    } 
  }
}
