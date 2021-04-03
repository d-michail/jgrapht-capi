/*
 * (C) Copyright 2020-2021, by Dimitrios Michail.
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
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.PointerBase;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.ExternalRef;
import org.jgrapht.capi.graph.HashAndEqualsResolver;

public class IsomorphismApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "isomorphism_exec_vf2", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeVF2(IsolateThread thread, ObjectHandle graph1Handle, ObjectHandle graph2Handle,
			CIntPointer existsRes, WordPointer graphMappingIteratorRes) {
		Graph<V, E> g1 = globalHandles.get(graph1Handle);
		Graph<V, E> g2 = globalHandles.get(graph2Handle);

		VF2GraphIsomorphismInspector<V, E> alg = new VF2GraphIsomorphismInspector<>(g1, g2);
		boolean exists = alg.isomorphismExists();
		if (existsRes.isNonNull()) {
			existsRes.write(exists ? 1 : 0);
		}
		if (exists && graphMappingIteratorRes.isNonNull()) {
			graphMappingIteratorRes.write(globalHandles.create(alg.getMappings()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	/**
	 * Note that this inspector only finds isomorphisms between a smaller graph and
	 * all
	 * <a href="http://mathworld.wolfram.com/Vertex-InducedSubgraph.html">induced
	 * subgraphs</a> of a larger graph. It does not find isomorphisms between the
	 * smaller graph and arbitrary subgraphs of the larger graph.
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_ANY
			+ "isomorphism_exec_vf2_subgraph", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeVF2Subgraph(IsolateThread thread, ObjectHandle graph1Handle,
			ObjectHandle graph2Handle, CIntPointer existsRes, WordPointer graphMappingIteratorRes) {
		Graph<V, E> g1 = globalHandles.get(graph1Handle);
		Graph<V, E> g2 = globalHandles.get(graph2Handle);

		VF2SubgraphIsomorphismInspector<V, E> alg = new VF2SubgraphIsomorphismInspector<>(g1, g2);
		boolean exists = alg.isomorphismExists();
		if (existsRes.isNonNull()) {
			existsRes.write(exists ? 1 : 0);
		}
		if (exists && graphMappingIteratorRes.isNonNull()) {
			graphMappingIteratorRes.write(globalHandles.create(alg.getMappings()));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_INT
			+ "isomorphism_graph_mapping_edge_correspondence", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphMappingEdge(IsolateThread thread, ObjectHandle mappingHandle, int edge, boolean forward,
			CIntPointer existsEdgeRes, CIntPointer edgeRes) {
		GraphMapping<?, Integer> graphMapping = globalHandles.get(mappingHandle);

		Integer otherEdge = graphMapping.getEdgeCorrespondence(edge, forward);
		if (existsEdgeRes.isNonNull()) {
			if (otherEdge != null) {
				existsEdgeRes.write(1);
				if (edgeRes.isNonNull()) {
					edgeRes.write(otherEdge);
				}
			} else {
				existsEdgeRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_LONG
			+ "isomorphism_graph_mapping_edge_correspondence", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphMappingEdge(IsolateThread thread, ObjectHandle mappingHandle, long edge, boolean forward,
			CIntPointer existsEdgeRes, CLongPointer edgeRes) {
		GraphMapping<?, Long> graphMapping = globalHandles.get(mappingHandle);

		Long otherEdge = graphMapping.getEdgeCorrespondence(edge, forward);
		if (existsEdgeRes.isNonNull()) {
			if (otherEdge != null) {
				existsEdgeRes.write(1);
				if (edgeRes.isNonNull()) {
					edgeRes.write(otherEdge);
				}
			} else {
				existsEdgeRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANY_DREF
			+ "isomorphism_graph_mapping_edge_correspondence", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphMappingEdge(IsolateThread thread, ObjectHandle mappingHandle, PointerBase edgePtr,
			ObjectHandle hashEqualsResolverHandle, boolean forward, CIntPointer existsEdgeRes, WordPointer edgeRes) {
		GraphMapping<?, ExternalRef> graphMapping = globalHandles.get(mappingHandle);

		HashAndEqualsResolver resolver = globalHandles.get(hashEqualsResolverHandle);
		ExternalRef edge = resolver.toExternalRef(edgePtr);

		ExternalRef otherEdge = graphMapping.getEdgeCorrespondence(edge, forward);
		if (existsEdgeRes.isNonNull()) {
			if (otherEdge != null) {
				existsEdgeRes.write(1);
				if (edgeRes.isNonNull()) {
					edgeRes.write(otherEdge.getPtr());
				}
			} else {
				existsEdgeRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INT_ANY
			+ "isomorphism_graph_mapping_vertex_correspondence", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphMappingVertex(IsolateThread thread, ObjectHandle mappingHandle, int vertex, boolean forward,
			CIntPointer existsVertexRes, CIntPointer vertexRes) {
		GraphMapping<Integer, ?> graphMapping = globalHandles.get(mappingHandle);

		Integer otherVertex = graphMapping.getVertexCorrespondence(vertex, forward);
		if (existsVertexRes.isNonNull()) {
			if (otherVertex != null) {
				existsVertexRes.write(1);
				if (vertexRes.isNonNull()) {
					vertexRes.write(otherVertex);
				}
			} else {
				existsVertexRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONG_ANY
			+ "isomorphism_graph_mapping_vertex_correspondence", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphMappingVertex(IsolateThread thread, ObjectHandle mappingHandle, long vertex, boolean forward,
			CIntPointer existsVertexRes, CLongPointer vertexRes) {
		GraphMapping<Long, ?> graphMapping = globalHandles.get(mappingHandle);

		Long otherVertex = graphMapping.getVertexCorrespondence(vertex, forward);
		if (existsVertexRes.isNonNull()) {
			if (otherVertex != null) {
				existsVertexRes.write(1);
				if (vertexRes.isNonNull()) {
					vertexRes.write(otherVertex);
				}
			} else {
				existsVertexRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.DREF_ANY
			+ "isomorphism_graph_mapping_vertex_correspondence", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int graphMappingVertex(IsolateThread thread, ObjectHandle mappingHandle, PointerBase vertexPtr,
			ObjectHandle hashEqualsResolverHandle, boolean forward, CIntPointer existsVertexRes,
			WordPointer vertexRes) {
		GraphMapping<ExternalRef, ?> graphMapping = globalHandles.get(mappingHandle);
		HashAndEqualsResolver resolver = globalHandles.get(hashEqualsResolverHandle);
		ExternalRef vertex = resolver.toExternalRef(vertexPtr);

		ExternalRef otherVertex = graphMapping.getVertexCorrespondence(vertex, forward);
		if (existsVertexRes.isNonNull()) {
			if (otherVertex != null) {
				existsVertexRes.write(1);
				if (vertexRes.isNonNull()) {
					vertexRes.write(otherVertex.getPtr());
				}
			} else {
				existsVertexRes.write(0);
			}
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
