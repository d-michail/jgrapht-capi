#include <stdio.h>
#include <stdlib.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>


int main() {
    
#ifdef _WIN32
    _CrtSetReportMode( _CRT_WARN, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_WARN, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ERROR, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ERROR, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ASSERT, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ASSERT, _CRTDBG_FILE_STDERR);
#endif

    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 1, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int flag;
    assert(jgrapht_capi_xx_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_xx_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_xx_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_xx_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_xx_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 0);

    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 4, 0, NULL);

    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 0, 5.0);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 1, 4.0);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 2, 3.0);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 3, 2.0);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 4, 1.0);

    // run kruskal
    void *mst;
    double weight;
    assert(jgrapht_capi_xx_mst_exec_kruskal(thread, g, &weight, &mst) == 0);
    assert(weight == 10.0);
    int size;
    jgrapht_capi_set_size(thread, mst, &size);
    assert(size == 4);
    int contains;
    jgrapht_capi_set_int_contains(thread, mst, 1, &contains);
    assert(contains);
    jgrapht_capi_set_int_contains(thread, mst, 2, &contains);
    assert(contains);
    jgrapht_capi_set_int_contains(thread, mst, 3, &contains);
    assert(contains);
    jgrapht_capi_set_int_contains(thread, mst, 4, &contains);
    assert(contains);
    jgrapht_capi_handles_destroy(thread,  mst);

    // run prim
    assert(jgrapht_capi_xx_mst_exec_prim(thread, g, &weight, &mst) == 0);
    assert(weight == 10.0);
    jgrapht_capi_set_size(thread, mst, &size);
    assert(size == 4);
    jgrapht_capi_set_int_contains(thread, mst, 1, &contains);
    assert(contains);
    jgrapht_capi_set_int_contains(thread, mst, 2, &contains);
    assert(contains);
    jgrapht_capi_set_int_contains(thread, mst, 3, &contains);
    assert(contains);
    jgrapht_capi_set_int_contains(thread, mst, 4, &contains);
    assert(contains);
    jgrapht_capi_handles_destroy(thread,  mst);

    // run boruvka
    assert(jgrapht_capi_xx_mst_exec_boruvka(thread, g, &weight, &mst) == 0);
    assert(weight == 10.0);
    jgrapht_capi_handles_destroy(thread,  mst);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
