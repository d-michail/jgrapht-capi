/*
 * (C) Copyright 2020, by Dimitrios Michail.
 *
 * JGraphT C-API
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.capi.impl;

import java.io.File;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.enums.ExporterDIMACSFormat;
import org.jgrapht.capi.enums.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.nio.dimacs.DIMACSExporter;
import org.jgrapht.nio.dimacs.DIMACSFormat;

public class ExporterApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "export_file_dimacs_sp", exceptionHandler = StatusReturnExceptionHandler.class)
	public static Status exportDIMACSShortestPathToFile(IsolateThread thread, ObjectHandle graphHandle,
			ExporterDIMACSFormat format, CCharPointer filename) {
		Graph<Long, Long> g = globalHandles.get(graphHandle);

		DIMACSFormat actualFormat = null;
		switch (format) {
		case COLORING:
			actualFormat = DIMACSFormat.COLORING;
			break;
		case MAX_CLIQUE:
			actualFormat = DIMACSFormat.MAX_CLIQUE;
			break;
		default:
			actualFormat = DIMACSFormat.SHORTEST_PATH;
			break;
		}

		DIMACSExporter<Long, Long> exporter = new DIMACSExporter<>(x -> String.valueOf(x), actualFormat);
		exporter.exportGraph(g, new File(CTypeConversion.toJavaString(filename)));
		return Status.SUCCESS;
	}

}
