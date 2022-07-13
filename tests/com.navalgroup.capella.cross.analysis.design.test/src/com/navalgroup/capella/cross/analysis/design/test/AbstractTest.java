/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.tools.api.command.ui.NoUICallback;
import org.eclipse.sirius.tools.api.command.ui.UICallBack;
import org.junit.After;
import org.junit.Before;
import org.osgi.framework.Bundle;
import org.polarsys.capella.common.helpers.EcoreUtil2;
import org.polarsys.capella.core.data.capellamodeller.Project;
import org.polarsys.capella.core.libraries.utils.IFileRequestor;
import org.polarsys.capella.core.model.handler.command.CapellaResourceHelper;

/**
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public abstract class AbstractTest {

	private static final String TEST_FOLDER = "resources/dependencies/";

	private static final String TEST_PROJECT_NAME = "com.navalgroup.capella.cross.analysis.design.test";

	private static final String IN_FLIGHT_ENTERTAINMENT_SYSTEM = "In-FlightEntertainmentSystem";

	protected IProject testProject;

	protected Session session;

	protected Project modelRoot;

	/**
	 * Constructor.
	 * 
	 * @param testFolder
	 *            The test folder path.
	 * @throws IOException
	 *             If the Capella model cannot be read.
	 */
	public AbstractTest() {
	}

	@Before
	public void setUp() throws IOException, CoreException {
		createProject();
		openSession(testProject);
		assertNotNull(testProject);

		modelRoot = findRootProject(session).orElseThrow(IllegalArgumentException::new);
		assertNotNull(modelRoot);
	}

	public void createProject() throws CoreException, IOException {
		Bundle bundle = Platform.getBundle(TEST_PROJECT_NAME);
		URL entry = bundle.getEntry(TEST_FOLDER + IN_FLIGHT_ENTERTAINMENT_SYSTEM);
		String testSource = FileLocator.toFileURL(entry).getFile();
		if (testSource.startsWith("/")) {
			testSource = testSource.substring(1);
		}
		String source = testSource;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String target = root.getLocation().toPortableString() + "/" + IN_FLIGHT_ENTERTAINMENT_SYSTEM;

		testProject = root.getProject(IN_FLIGHT_ENTERTAINMENT_SYSTEM);
		if (testProject.exists()) {
			testProject.delete(true, new NullProgressMonitor());
		}
		Files.walk(Paths.get(source)).forEach(s -> {
			try {
				Path destination = Paths.get(target,
						s.toString().substring(Paths.get(source).toFile().getAbsolutePath().length()));
				Files.copy(s, destination);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		testProject.create(new NullProgressMonitor());
		testProject.open(new NullProgressMonitor());
		testProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	@After
	public void tearDown() throws CoreException {
		if (testProject.exists()) {
			testProject.delete(true, true, new NullProgressMonitor());
		}
	}

	public Optional<Project> findRootProject(Session session) {
		return session.getSemanticResources().stream().flatMap(r -> r.getContents().stream())
				.filter(Project.class::isInstance).map(Project.class::cast).findFirst();
	}

	private void openSession(IProject project) {
		session = openSession(project, new NullProgressMonitor()).orElseThrow(IllegalArgumentException::new);
	}

	public static Optional<Session> openSession(IProject project, IProgressMonitor monitor) {
		List<IFile> files = getAirdFiles(project);
		if (!files.isEmpty()) {
			IFile sessionResourceFile = files.get(0);
			UICallBack uiCallBack = new NoUICallback();
			// one aird per project, fragments are always aird_fragment
			Session session = SessionManager.INSTANCE.openSession(EcoreUtil2.getURI(sessionResourceFile), monitor,
					uiCallBack);
			if (session == null) {
				throw new IllegalStateException(String.format("No session found", project.getName()));
			}
			return Optional.of(session);
		}
		return Optional.empty();
	}

	public static List<IFile> getAirdFiles(IProject project) {
		return new IFileRequestor().search(project, CapellaResourceHelper.AIRD_FILE_EXTENSION, false);
	}

}
