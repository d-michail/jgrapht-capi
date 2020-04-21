#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi.h>

#define NUM_VERTICES 1000
#define ITERATOR_NO_SUCH_ELEMENT 100

int main() { 
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }    

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g = jgrapht_capi_graph_create(thread, 1, 1, 1, 1);
    assert(jgrapht_capi_get_errno(thread) == 0);

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 1);

    long long v;
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 0);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 1);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 2);

    long long e;
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 0, &e) == 0);
    assert(e == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 1, &e) == 0);
    assert(e == 1);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 2, &e) == 0);
    assert(e == 2);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2, &e) == 0);
    assert(e == 3);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2, &e) == 0);
    assert(e == 4);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2, &e) == 0);
    assert(e == 5);

    long long s, t;
    assert(jgrapht_capi_graph_edge_source(thread, g, 0, &s) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 0, &t) == 0);
    assert(s == 0 && t == 0);
    assert(jgrapht_capi_graph_edge_source(thread, g, 1, &s) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 1, &t) == 0);
    assert(s == 0 && t == 1);
    assert(jgrapht_capi_graph_edge_source(thread, g, 2, &s) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 2, &t) == 0);
    assert(s == 0 && t == 2);
    assert(jgrapht_capi_graph_edge_source(thread, g, 3, &s) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 3, &t) == 0);
    assert(s == 1 && t == 2);
    assert(jgrapht_capi_graph_edge_source(thread, g, 4, &s) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 4, &t) == 0);
    assert(s == 1 && t == 2);
    assert(jgrapht_capi_graph_edge_source(thread, g, 5, &s) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 5, &t) == 0);
    assert(s == 1 && t == 2);

    double w;
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 0, &w) == 0);
    assert(w == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 1, &w) == 0);
    assert(w == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 2, &w) == 0);
    assert(w == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 3, &w) == 0);
    assert(w == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 4, &w) == 0);
    assert(w == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 5, &w) == 0);
    assert(w == 1.0);

    jgrapht_capi_graph_set_edge_weight(thread, g, 0, 5.0);
    assert(jgrapht_capi_get_errno(thread) == 0); 
    jgrapht_capi_graph_get_edge_weight(thread, g, 0, &w);
    assert(w == 5.0);

    void *eit = jgrapht_capi_graph_create_all_eit(thread, g);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 0);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 1);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 2);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 3);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 4);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 5);
    int hasnext;
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);

    jgrapht_capi_destroy(thread, eit);

    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 0, 0, &flag) == 0);
    assert(flag);
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 0, 1, &flag) == 0);
    assert(flag);
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 0, 2, &flag) == 0);
    assert(flag);
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 1, 0, &flag) == 0);
    assert(!flag);
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 1, 1, &flag) == 0);
    assert(!flag);
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 1, 2, &flag) == 0);
    assert(flag);
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 2, 0, &flag) == 0);
    assert(!flag);
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 2, 1, &flag) == 0);
    assert(!flag);
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 2, 2, &flag) == 0);
    assert(!flag);

    eit = jgrapht_capi_graph_create_between_eit(thread, g, 1 , 2);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 3);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 4);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == 5);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_destroy(thread, eit);

    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}