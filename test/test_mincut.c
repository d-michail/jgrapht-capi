#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>


int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int v;
    int e;
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 4, 0, NULL);

    jgrapht_capi_graph_set_edge_weight(thread, g, 0, 5.0);
    jgrapht_capi_graph_set_edge_weight(thread, g, 1, 4.0);
    jgrapht_capi_graph_set_edge_weight(thread, g, 2, 3.0);
    jgrapht_capi_graph_set_edge_weight(thread, g, 3, 2.0);
    jgrapht_capi_graph_set_edge_weight(thread, g, 4, 1.0);

    void *mincut;
    double weight;
    jgrapht_capi_cut_exec_stoer_wagner(thread, g, &weight, &mincut);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(weight == 3.0);

    jgrapht_capi_handles_destroy(thread,  mincut);
    jgrapht_capi_handles_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}