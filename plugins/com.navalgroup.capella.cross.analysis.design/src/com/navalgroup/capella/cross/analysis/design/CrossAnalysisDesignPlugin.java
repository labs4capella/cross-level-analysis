/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.ui.EclipseUIPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public class CrossAnalysisDesignPlugin extends EMFPlugin {
	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.navalgroup.capella.cross.analysis.design";

	/**
	 * The shared instance.
	 */
	public static final CrossAnalysisDesignPlugin INSTANCE = new CrossAnalysisDesignPlugin();

	/** The sole instance of the bundle activator. */
	private static Implementation plugin;

	/**
	 * Viewpoints.
	 */
	private static Set<Viewpoint> viewpoints;

	/**
	 * The constructor.
	 */
	public CrossAnalysisDesignPlugin() {
		super(new ResourceLocator[0]);
	}

	@Override
	public ResourceLocator getPluginResourceLocator() {
		return plugin;
	}

	/**
	 * Returns the singleton instance of the Eclipse plugin.
	 * 
	 * @return the singleton instance.
	 */
	public static Implementation getPlugin() {
		return plugin;
	}

	/**
	 * The activator class controls the plug-in lifecycle.
	 */
	public static class Implementation extends EclipseUIPlugin implements BundleActivator {
		/**
		 * Returns current Capella version 
		 */
		private String getCapellaVersion() {
			String fullVersion = Platform.getBundle("org.polarsys.capella.rcp").getVersion().toString();
			String version = String.join(".", Arrays.copyOf(fullVersion.split("\\."), 3));
			logWarning("Capella Version : " + version);
			return version;
		}

		/**
		 * Constructor thanks to which the code works both in and outside of eclipse.
		 */
		public Implementation() {
			CrossAnalysisDesignPlugin.plugin = this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
		 *      BundleContext)
		 */
		public void start(BundleContext context) throws Exception {
			super.start(context);
			plugin = this;
			viewpoints = new HashSet<Viewpoint>();
			viewpoints.addAll(ViewpointRegistry.getInstance()
					.registerFromPlugin("/" + PLUGIN_ID + "/description/crossAnalysis-" + getCapellaVersion() + ".odesign"));
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
		 */
		public void stop(BundleContext context) throws Exception {
			plugin = null;
			if (viewpoints != null) {
				for (final Viewpoint viewpoint : viewpoints) {
					ViewpointRegistry.getInstance().disposeFromPlugin(viewpoint);
				}
				viewpoints.clear();
				viewpoints = null;
			}
			super.stop(context);
		}

		/**
		 * Logs an {@link IStatus#WARNING} in the Error Log.
		 * 
		 * @param message
		 *            the (maybe-{@code null}) {@link String message} to log.
		 */
		public void logWarning(String message) {
			logWarning(message, null);
		}

		/**
		 * Logs an {@link IStatus#ERROR} in the Error Log.
		 * 
		 * @param message
		 *            the (maybe-{@code null}) {@link String message} to log.
		 */
		public void logError(String message) {
			logError(message, null);
		}

		/**
		 * Log an {@link IStatus#WARNING} in the Error Log.
		 * 
		 * @param message
		 *            the (maybe-{@code null}) {@link String message} to log.
		 * @param cause
		 *            the (maybe-{@code null}) cause {@link Throwable}.
		 */
		public void logWarning(String message, Throwable cause) {
			getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, cause));
		}

		/**
		 * Log an {@link IStatus#ERROR} in the Error Log.
		 * 
		 * @param message
		 *            the (maybe-{@code null}) {@link String message} to log.
		 * @param cause
		 *            the (maybe-{@code null}) cause {@link Throwable}.
		 */
		public void logError(String message, Throwable cause) {
			getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, cause));
		}
	}

}
