package org.jgrapht.capi.impl;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.WordFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.BoykovKolmogorovMFImpl;
import org.jgrapht.alg.flow.DinicMFImpl;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.flow.GusfieldEquivalentFlowTree;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem.MinimumCostFlowProblemImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm.MinimumCostFlow;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.IntegerToIntegerFunctionPointer;
import org.jgrapht.capi.JGraphTContext.LongToIntegerFunctionPointer;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.JGraphTContext.VoidToLongFunctionPointer;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;
import org.jgrapht.capi.graph.DefaultCapiGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class FlowApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "maxflow_exec_push_relabel", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePushRelabel(IsolateThread thread, ObjectHandle graphHandle, int source, int sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, PushRelabelMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "maxflow_exec_push_relabel", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executePushRelabel(IsolateThread thread, ObjectHandle graphHandle, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunLongMaxFlow(thread, graphHandle, PushRelabelMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "maxflow_exec_dinic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDinic(IsolateThread thread, ObjectHandle graphHandle, int source, int sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, DinicMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "maxflow_exec_dinic", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeDinic(IsolateThread thread, ObjectHandle graphHandle, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunLongMaxFlow(thread, graphHandle, DinicMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "maxflow_exec_edmonds_karp", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEdmondsKarp(IsolateThread thread, ObjectHandle graphHandle, int source, int sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunMaxFlow(thread, graphHandle, EdmondsKarpMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "maxflow_exec_edmonds_karp", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeEdmondsKarp(IsolateThread thread, ObjectHandle graphHandle, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunLongMaxFlow(thread, graphHandle, EdmondsKarpMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "maxflow_exec_boykov_kolmogorov", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBoykovKolmogorov(IsolateThread thread, ObjectHandle graphHandle, int source, int sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunLongMaxFlow(thread, graphHandle, BoykovKolmogorovMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "maxflow_exec_boykov_kolmogorov", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeBoykovKolmogorov(IsolateThread thread, ObjectHandle graphHandle, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		return doRunLongMaxFlow(thread, graphHandle, BoykovKolmogorovMFImpl::new, source, sink, valueRes, flowRes,
				cutSourcePartitionRes);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "mincostflow_exec_capacity_scaling", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCapacityScaling(IsolateThread thread, ObjectHandle graphHandle,
			IntegerToIntegerFunctionPointer nodeSupplyFunction,
			IntegerToIntegerFunctionPointer arcCapacityLowerBoundsFunction,
			IntegerToIntegerFunctionPointer arcCapacityUpperBoundsFunction, int scalingFactor, CDoublePointer valueRes,
			WordPointer flowRes, WordPointer dualSolutionRes) {

		Graph<Integer, Integer> g = globalHandles.get(graphHandle);

		Function<Integer, Integer> nodeSupplies = v -> nodeSupplyFunction.invoke(v);
		Function<Integer, Integer> arcCapacityUpperBounds = e -> arcCapacityUpperBoundsFunction.invoke(e);
		Function<Integer, Integer> arcCapacityLowerBounds;
		if (arcCapacityLowerBoundsFunction.isNonNull()) {
			arcCapacityLowerBounds = e -> arcCapacityLowerBoundsFunction.invoke(e);
		} else {
			arcCapacityLowerBounds = e -> 0;
		}

		MinimumCostFlowProblemImpl<Integer, Integer> problem = new MinimumCostFlowProblemImpl<>(g, nodeSupplies,
				arcCapacityUpperBounds, arcCapacityLowerBounds);
		CapacityScalingMinimumCostFlow<Integer, Integer> alg = new CapacityScalingMinimumCostFlow<>(scalingFactor);

		MinimumCostFlow<Integer> flow = alg.getMinimumCostFlow(problem);
		double flowCost = flow.getCost();
		Map<Integer, Double> flowMap = flow.getFlowMap();
		Map<Integer, Double> dualMap = alg.getDualSolution();

		if (valueRes.isNonNull()) {
			valueRes.write(flowCost);
		}
		if (flowRes.isNonNull()) {
			flowRes.write(globalHandles.create(flowMap));
		}
		if (dualSolutionRes.isNonNull()) {
			dualSolutionRes.write(globalHandles.create(dualMap));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "mincostflow_exec_capacity_scaling", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeCapacityScaling(IsolateThread thread, ObjectHandle graphHandle,
			LongToIntegerFunctionPointer nodeSupplyFunction,
			LongToIntegerFunctionPointer arcCapacityLowerBoundsFunction,
			LongToIntegerFunctionPointer arcCapacityUpperBoundsFunction, int scalingFactor, CDoublePointer valueRes,
			WordPointer flowRes, WordPointer dualSolutionRes) {

		Graph<Long, Long> g = globalHandles.get(graphHandle);

		Function<Long, Integer> nodeSupplies = v -> nodeSupplyFunction.invoke(v);
		Function<Long, Integer> arcCapacityUpperBounds = e -> arcCapacityUpperBoundsFunction.invoke(e);
		Function<Long, Integer> arcCapacityLowerBounds;
		if (arcCapacityLowerBoundsFunction.isNonNull()) {
			arcCapacityLowerBounds = e -> arcCapacityLowerBoundsFunction.invoke(e);
		} else {
			arcCapacityLowerBounds = e -> 0;
		}

		MinimumCostFlowProblemImpl<Long, Long> problem = new MinimumCostFlowProblemImpl<>(g, nodeSupplies,
				arcCapacityUpperBounds, arcCapacityLowerBounds);
		CapacityScalingMinimumCostFlow<Long, Long> alg = new CapacityScalingMinimumCostFlow<>(scalingFactor);

		MinimumCostFlow<Long> flow = alg.getMinimumCostFlow(problem);
		double flowCost = flow.getCost();
		Map<Long, Double> flowMap = flow.getFlowMap();
		Map<Long, Double> dualMap = alg.getDualSolution();

		if (valueRes.isNonNull()) {
			valueRes.write(flowCost);
		}
		if (flowRes.isNonNull()) {
			flowRes.write(globalHandles.create(flowMap));
		}
		if (dualSolutionRes.isNonNull()) {
			dualSolutionRes.write(globalHandles.create(dualMap));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "equivalentflowtree_exec_gusfield", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <V, E> int executeEFTGusfield(IsolateThread thread, ObjectHandle graphHandle, WordPointer res) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		GusfieldEquivalentFlowTree<V, E> alg = new GusfieldEquivalentFlowTree<>(g);
		if (res.isNonNull()) {
			res.write(globalHandles.create(alg));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTANY
			+ "equivalentflowtree_max_st_flow", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int eftMaxSTFlow(IsolateThread thread, ObjectHandle eft, int source, int sink,
			CDoublePointer valueRes) {
		GusfieldEquivalentFlowTree<Integer, E> alg = globalHandles.get(eft);
		double flowValue = alg.getMaximumFlowValue(source, sink);
		if (valueRes.isNonNull()) {
			valueRes.write(flowValue);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGANY
			+ "equivalentflowtree_max_st_flow", exceptionHandler = StatusReturnExceptionHandler.class)
	public static <E> int eftMaxSTFlow(IsolateThread thread, ObjectHandle eft, long source, long sink,
			CDoublePointer valueRes) {
		GusfieldEquivalentFlowTree<Long, E> alg = globalHandles.get(eft);
		double flowValue = alg.getMaximumFlowValue(source, sink);
		if (valueRes.isNonNull()) {
			valueRes.write(flowValue);
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.INTINT
			+ "equivalentflowtree_tree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int eftGetTree(IsolateThread thread, ObjectHandle eft, WordPointer treeRes) {
		GusfieldEquivalentFlowTree<Integer, Integer> alg = globalHandles.get(eft);
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> origTree = alg.getEquivalentFlowTree();

		// convert to integer vertices/edges
		Graph<Integer, Integer> tree = GraphApi.createGraph(false, false, false, true, WordFactory.nullPointer(),
				WordFactory.nullPointer());
		tree = new DefaultCapiGraph<Integer, Integer>(tree);

		for (Integer v : origTree.vertexSet()) {
			tree.addVertex(v);
		}
		for (DefaultWeightedEdge e : origTree.edgeSet()) {
			int s = origTree.getEdgeSource(e);
			int t = origTree.getEdgeTarget(e);
			double w = origTree.getEdgeWeight(e);
			tree.setEdgeWeight(tree.addEdge(s, t), w);
		}

		// write result
		if (treeRes.isNonNull()) {
			treeRes.write(globalHandles.create(tree));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "equivalentflowtree_tree", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int llEftGetTree(IsolateThread thread, ObjectHandle eft, WordPointer treeRes) {
		GusfieldEquivalentFlowTree<Long, Long> alg = globalHandles.get(eft);
		SimpleWeightedGraph<Long, DefaultWeightedEdge> origTree = alg.getEquivalentFlowTree();

		// convert to integer vertices/edges
		Graph<Long, Long> tree = GraphApi.createLongGraph(false, false, false, true, WordFactory.nullPointer(),
				WordFactory.nullPointer());
		tree = new DefaultCapiGraph<Long, Long>(tree);

		for (Long v : origTree.vertexSet()) {
			tree.addVertex(v);
		}
		for (DefaultWeightedEdge e : origTree.edgeSet()) {
			long s = origTree.getEdgeSource(e);
			long t = origTree.getEdgeTarget(e);
			double w = origTree.getEdgeWeight(e);
			tree.setEdgeWeight(tree.addEdge(s, t), w);
		}

		// write result
		if (treeRes.isNonNull()) {
			treeRes.write(globalHandles.create(tree));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.LONGLONG
			+ "equivalentflowtree_tree_with_suppliers", exceptionHandler = StatusReturnExceptionHandler.class, documentation = {
					"Given an instance of the equivalent flow tree from Gusfield's algorithm, compute",
					"the actual tree as a graph. The new graph will reuse the vertex set from the original graph",
					"but will have new edges which will be constructed by the provided edge supplier." })
	public static int llEftGetTree(IsolateThread thread, ObjectHandle eft, VoidToLongFunctionPointer vertexSupplier,
			VoidToLongFunctionPointer edgeSupplier, WordPointer treeRes) {
		GusfieldEquivalentFlowTree<Long, Long> alg = globalHandles.get(eft);
		SimpleWeightedGraph<Long, DefaultWeightedEdge> origTree = alg.getEquivalentFlowTree();

		// convert to integer vertices/edges
		Graph<Long, Long> tree = GraphApi.createLongGraph(false, false, false, true, vertexSupplier, edgeSupplier);
		tree = new DefaultCapiGraph<Long, Long>(tree);

		for (Long v : origTree.vertexSet()) {
			tree.addVertex(v);
		}
		for (DefaultWeightedEdge e : origTree.edgeSet()) {
			long s = origTree.getEdgeSource(e);
			long t = origTree.getEdgeTarget(e);
			double w = origTree.getEdgeWeight(e);
			tree.setEdgeWeight(tree.addEdge(s, t), w);
		}

		// write result
		if (treeRes.isNonNull()) {
			treeRes.write(globalHandles.create(tree));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static <E> int doRunMaxFlow(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Integer, E>, MaximumFlowAlgorithmBase<Integer, E>> algProvider, int source,
			int sink, CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		Graph<Integer, E> g = globalHandles.get(graphHandle);
		MaximumFlowAlgorithmBase<Integer, E> alg = algProvider.apply(g);
		MaximumFlow<E> maximumFlow = alg.getMaximumFlow(source, sink);
		Map<E, Double> flowMap = maximumFlow.getFlowMap();
		Set<Integer> cutSourcePartition = alg.getSourcePartition();
		double flowValue = maximumFlow.getValue();
		if (valueRes.isNonNull()) {
			valueRes.write(flowValue);
		}
		if (flowRes.isNonNull()) {
			flowRes.write(globalHandles.create(flowMap));
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(cutSourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static <E> int doRunLongMaxFlow(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<Long, E>, MaximumFlowAlgorithmBase<Long, E>> algProvider, long source, long sink,
			CDoublePointer valueRes, WordPointer flowRes, WordPointer cutSourcePartitionRes) {
		Graph<Long, E> g = globalHandles.get(graphHandle);
		MaximumFlowAlgorithmBase<Long, E> alg = algProvider.apply(g);
		MaximumFlow<E> maximumFlow = alg.getMaximumFlow(source, sink);
		Map<E, Double> flowMap = maximumFlow.getFlowMap();
		Set<Long> cutSourcePartition = alg.getSourcePartition();
		double flowValue = maximumFlow.getValue();
		if (valueRes.isNonNull()) {
			valueRes.write(flowValue);
		}
		if (flowRes.isNonNull()) {
			flowRes.write(globalHandles.create(flowMap));
		}
		if (cutSourcePartitionRes.isNonNull()) {
			cutSourcePartitionRes.write(globalHandles.create(cutSourcePartition));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
