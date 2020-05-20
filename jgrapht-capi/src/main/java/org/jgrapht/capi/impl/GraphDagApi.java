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

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.SafeEdgeSupplier;
import org.jgrapht.capi.graph.SafeVertexSupplier;
import org.jgrapht.graph.DirectedAcyclicGraph;

public class GraphDagApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Create a dag and return its handle.
	 *
	 * @param thread the thread isolate
	 * @return the graph handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_dag_create", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createDag(IsolateThread thread, boolean weighted, boolean allowMultipleEdges, WordPointer res) {
		SafeVertexSupplier vSupplier = new SafeVertexSupplier();
		SafeEdgeSupplier eSupplier = new SafeEdgeSupplier();

		Graph<Integer, Integer> graph = new DirectedAcyclicGraph<>(vSupplier, eSupplier, weighted, allowMultipleEdges);

		vSupplier.setGraph(graph);
		eSupplier.setGraph(graph);

		if (res.isNonNull()) {
			res.write(globalHandles.create(graph));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_dag_topological_it", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createTopoIterator(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		DirectedAcyclicGraph<Integer, Integer> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.iterator()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_dag_vertex_descendants", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createVertexDescendants(IsolateThread thread, ObjectHandle graphHandle, int vertex,
			WordPointer res) {
		DirectedAcyclicGraph<Integer, Integer> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.getDescendants(vertex)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX
			+ "graph_dag_vertex_ancestors", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int createVertexAncestors(IsolateThread thread, ObjectHandle graphHandle, int vertex,
			WordPointer res) {
		DirectedAcyclicGraph<Integer, Integer> graph = globalHandles.get(graphHandle);
		if (res.isNonNull()) {
			res.write(globalHandles.create(graph.getAncestors(vertex)));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
