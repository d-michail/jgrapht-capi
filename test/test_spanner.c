#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

#define ITERATOR_NO_SUCH_ELEMENT 100

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 0);

    long long v;
    long long e;
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

    // run 
    void *spanner;
    double weight;
    assert(jgrapht_capi_spanner_exec_greedy_multiplicative(thread, g, 3, &weight, &spanner)==0);
    assert(weight == 4.0);
    long long size;
    jgrapht_capi_set_size(thread, spanner, &size);
    assert(size == 4);
    int contains;
    jgrapht_capi_set_long_contains(thread, spanner, 0, &contains);
    assert(contains);
    jgrapht_capi_set_long_contains(thread, spanner, 1, &contains);
    assert(contains);
    jgrapht_capi_set_long_contains(thread, spanner, 2, &contains);
    assert(contains);
    jgrapht_capi_set_long_contains(thread, spanner, 3, &contains);
    assert(contains);
    jgrapht_capi_set_long_contains(thread, spanner, 4, &contains);
    assert(!contains);
    jgrapht_capi_destroy(thread,  spanner);

    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
