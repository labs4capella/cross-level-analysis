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

import com.navalgroup.capella.permission.extension.PermissionRegistryListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.ui.EclipseUIPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PermissionPlugin extends EMFPlugin {
  public static final String PLUGIN_ID = "com.navalgroup.capella.permission";
  
  public static final PermissionPlugin INSTANCE = new PermissionPlugin();
  
  private static Implementation plugin;
  
  public PermissionPlugin() {
    super(new ResourceLocator[0]);
  }
  
  public ResourceLocator getPluginResourceLocator() {
    return (ResourceLocator)plugin;
  }
  
  public static Implementation getPlugin() {
    return plugin;
  }
  
  public static class Implementation extends EclipseUIPlugin implements BundleActivator {
    private PermissionRegistryListener permissionRegistryListener;
    
    public Implementation() {
      PermissionPlugin.plugin = this;
    }
    
    public void start(BundleContext context) throws Exception {
      super.start(context);
      PermissionPlugin.plugin = this;
      this.permissionRegistryListener = new PermissionRegistryListener();
      this.permissionRegistryListener.init();
    }
    
    public void stop(BundleContext context) throws Exception {
      this.permissionRegistryListener.dispose();
      PermissionPlugin.plugin = null;
      super.stop(context);
    }
    
    public void logWarning(String message) {
      logWarning(message, null);
    }
    
    public void logError(String message) {
      logError(message, null);
    }
    
    public void logWarning(String message, Throwable cause) {
      getLog().log((IStatus)new Status(2, "com.navalgroup.capella.permission", message, cause));
    }
    
    public void logError(String message, Throwable cause) {
      getLog().log((IStatus)new Status(4, "com.navalgroup.capella.permission", message, cause));
    }
  }
}
