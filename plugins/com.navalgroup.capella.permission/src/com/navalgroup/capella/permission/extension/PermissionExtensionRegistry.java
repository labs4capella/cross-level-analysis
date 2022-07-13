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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class PermissionExtensionRegistry {
  private static Collection<PermissionExtensionDescriptor> extensions = new HashSet<>();
  
  public static void addExtension(PermissionExtensionDescriptor extension) {
    extensions.add(extension);
  }
  
  public static void removeExtension(String extensionClassName) {
    Collection<PermissionExtensionDescriptor> values = new HashSet<>(extensions);
    for (PermissionExtensionDescriptor extension : values) {
      if (extension.getExtensionClassName().equals(extensionClassName))
        extensions.remove(extension); 
    } 
  }
  
  public static Collection<PermissionExtensionDescriptor> getRegisteredExtensions() {
    Set<PermissionExtensionDescriptor> registeredExtensions = new LinkedHashSet<>();
    for (PermissionExtensionDescriptor extension : extensions)
      registeredExtensions.add(extension); 
    return registeredExtensions;
  }
  
  public static PermissionExtensionDescriptor getHighestPriorityRegisteredExtension(String key) {
    PermissionExtensionDescriptor highestPriorityExtension = null;
    Integer highestPriority = Integer.valueOf(-1);
    Collection<PermissionExtensionDescriptor> filteredExtensions = (Collection<PermissionExtensionDescriptor>)extensions.stream()
      .filter(e -> key.equals(e.getKey())).collect(Collectors.toList());
    for (PermissionExtensionDescriptor extension : filteredExtensions) {
      Integer priority = extension.getPriority();
      if (priority.intValue() > highestPriority.intValue()) {
        highestPriority = priority;
        highestPriorityExtension = extension;
      } 
    } 
    return highestPriorityExtension;
  }
  
  public static IPermission getHighestPriorityPermission(String key) {
    String permissionKey = key;
    if (permissionKey == null || permissionKey.isEmpty())
      permissionKey = "capella.permission.default"; 
    PermissionExtensionDescriptor highestPriorityExtension = getHighestPriorityRegisteredExtension(permissionKey);
    if (highestPriorityExtension != null)
      return highestPriorityExtension.getPermission(); 
    return null;
  }
  
  public static void clearRegistry() {
    extensions.clear();
  }
}
