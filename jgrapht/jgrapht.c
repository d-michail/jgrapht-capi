#include <stdlib.h>
#include <stdio.h>
#include <jgrapht_nlib.h>

static graal_isolate_t *isolate = NULL;
static graal_isolatethread_t *thread = NULL;

void jgrapht_thread_create() { 
    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }
}

void jgrapht_thread_destroy() { 
    if (thread != NULL) { 
        if (graal_detach_thread(thread) != 0) {
                fprintf(stderr, "graal_detach_thread error\n");
                exit(EXIT_FAILURE);
        }
    }
}

int jgrapht_get_errno() { 
    return jgrapht_nlib_get_errno(thread);
}

void jgrapht_clear_errno() { 
    jgrapht_nlib_clear_errno(thread);
}

void jgrapht_destroy(void *handle) { 
    jgrapht_nlib_destroy(thread, handle);
}

long long int jgrapht_it_next(void *itHandle) { 
    return jgrapht_nlib_it_next(thread, itHandle);
}

int jgrapht_it_hasnext(void *itHandle) { 
    return jgrapht_nlib_it_hasnext(thread, itHandle);
}

void * jgrapht_create_graph(int directed, int allowingSelfLoops, int allowingMultipleEdges) { 
    return jgrapht_nlib_create_graph(thread, directed, allowingSelfLoops, allowingMultipleEdges);
}

long long int jgrapht_graph_vertices_count(void *gHandle) { 
    return jgrapht_nlib_graph_vertices_count(thread, gHandle);
}

long long int jgrapht_graph_edges_count(void *gHandle) { 
    return jgrapht_nlib_graph_edges_count(thread, gHandle);
}

long long int jgrapht_graph_add_vertex(void *gHandle) { 
    return jgrapht_nlib_graph_add_vertex(thread, gHandle);
}

int jgrapht_graph_remove_vertex(void *gHandle, long long int vertex) { 
    return jgrapht_nlib_graph_remove_vertex(thread, gHandle, vertex);
}

int jgrapht_graph_contains_vertex(void *gHandle, long long int vertex) { 
    return jgrapht_nlib_graph_contains_vertex(thread, gHandle, vertex);
}

long long int jgrapht_graph_add_edge(void *gHandle, long long int source, long long int target) { 
    return jgrapht_nlib_graph_add_edge(thread, gHandle, source, target);
}

int jgrapht_graph_remove_edge(void *gHandle, long long int edge) { 
    return jgrapht_nlib_graph_remove_edge(thread, gHandle, edge);
}

int jgrapht_graph_contains_edge(void *gHandle, long long int edge) { 
    return jgrapht_nlib_graph_contains_edge(thread, gHandle, edge);
}

long long int jgrapht_graph_degree_of(void *gHandle, long long int vertex) { 
    return jgrapht_nlib_graph_degree_of(thread, gHandle, vertex); 
}

long long int jgrapht_graph_indegree_of(void *gHandle, long long int vertex) { 
    return jgrapht_nlib_graph_indegree_of(thread, gHandle, vertex);
}

long long int jgrapht_graph_outdegree_of(void *gHandle, long long int vertex) { 
    return jgrapht_nlib_graph_outdegree_of(thread, gHandle, vertex);
}

long long int jgrapht_graph_edge_source(void *gHandle, long long int edge) { 
    return jgrapht_nlib_graph_edge_source(thread, gHandle, edge);
}

long long int jgrapht_graph_edge_target(void *gHandle, long long int edge) { 
    return jgrapht_nlib_graph_edge_target(thread, gHandle, edge);
}

int jgrapht_graph_is_weighted(void *gHandle) { 
    return jgrapht_nlib_graph_is_weighted(thread, gHandle);
}

int jgrapht_graph_is_directed(void *gHandle) { 
    return jgrapht_nlib_graph_is_directed(thread, gHandle);
}

int jgrapht_graph_is_undirected(void *gHandle) { 
    return jgrapht_nlib_graph_is_undirected(thread, gHandle);
}

int jgrapht_graph_is_allowing_selfloops(void *gHandle) { 
    return jgrapht_nlib_graph_is_allowing_selfloops(thread, gHandle);
}

int jgrapht_graph_is_allowing_multipleedges(void *gHandle) { 
    return jgrapht_nlib_graph_is_allowing_multipleedges(thread, gHandle);
}

double jgrapht_graph_get_edge_weight(void *gHandle, long long int edge) { 
    return jgrapht_nlib_graph_get_edge_weight(thread, gHandle, edge);
}

void jgrapht_graph_set_edge_weight(void *gHandle, long long int edge, double weight) { 
    return jgrapht_nlib_graph_set_edge_weight(thread, gHandle, edge, weight);
}

void * jgrapht_graph_create_all_vit(void *gHandle) { 
    return jgrapht_nlib_graph_create_all_vit(thread, gHandle);
}

void * jgrapht_graph_create_all_eit(void *gHandle) { 
    return jgrapht_nlib_graph_create_all_eit(thread, gHandle);
}

void * jgrapht_graph_vertex_create_eit(void *gHandle, long long int vertex) { 
    return jgrapht_nlib_graph_vertex_create_eit(thread, gHandle, vertex);
}

void * jgrapht_graph_vertex_create_out_eit(void *gHandle, long long int vertex) {
    return jgrapht_nlib_graph_vertex_create_out_eit(thread, gHandle, vertex);
}

void * jgrapht_graph_vertex_create_in_eit(void *gHandle, long long int vertex) {
    return jgrapht_nlib_graph_vertex_create_in_eit(thread, gHandle, vertex);
}

void * jgrapht_mst_exec_kruskal(void *gHandle) { 
    return jgrapht_nlib_mst_exec_kruskal(thread, gHandle);
}

void * jgrapht_mst_exec_prim(void *gHandle) {
    return jgrapht_nlib_mst_exec_prim(thread, gHandle);
}

double jgrapht_mst_get_weight(void *mstHandle) {
    return jgrapht_nlib_mst_get_weight(thread, mstHandle);
}

void * jgrapht_mst_create_eit(void *mstHandle) {
    return jgrapht_nlib_mst_create_eit(thread, mstHandle);
}

void jgrapht_vmLocatorSymbol() {
    vmLocatorSymbol(thread);
}






