/*
 * @file ccnd/loadsharing_strategy.c
 *
 * Part of ccnd - the CCNx Daemon
 *
 * Copyright (C) 2013 Palo Alto Research Center, Inc.
 *
 * This work is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 * This work is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <limits.h>
#include "ccnd_strategy.h"

#include "ccnd_private.h" // XXX - should work to remove this by extending strategy API

/**
 * This implements a distribution by performance strategy.
 *
 * The number of pending interests is a proxy for the performance of the face,
 * an interest is sent on the face with the minimum pending, or randomly to
 * one selected from those with the minimum.
 */

void
ccnd_loadsharing_strategy_impl(struct ccnd_handle *h,
                               struct strategy_instance *instance,
                               struct ccn_strategy *strategy,
                               enum ccn_strategy_op op,
                               unsigned faceid)
{
    struct pit_face_item *p = NULL;
    unsigned count;
    int best;
    unsigned smallestq;
    unsigned upending;
    struct face *face;
    
    switch (op) {
        case CCNST_NOP:
            break;
        case CCNST_INIT:
            break;
        case CCNST_FIRST:
            break;
        case CCNST_UPDATE:
            count = 0;
            smallestq = INT_MAX;
            upending = 0;
            for (p = strategy->pfl; p!= NULL; p = p->next) {
                if ((p->pfi_flags & CCND_PFI_UPENDING) != 0)
                    upending++;
            }
            if (upending == 0) {
                for (p = strategy->pfl; p!= NULL; p = p->next) {
                    if ((p->pfi_flags & CCND_PFI_ATTENTION) == 0)
                        continue;
                    face = ccnd_face_from_faceid(h, p->faceid);
                    if (face->outstanding_interests < smallestq) {
                        count = 1;
                        smallestq = face->outstanding_interests;
                    } else if (face->outstanding_interests == smallestq) {
                        count++;
                    }
                }
                
                if (count > 0) {
                    best = ccnd_random(h) % count;
                    for (p = strategy->pfl; p!= NULL; p = p->next) {
                        if ((p->pfi_flags & CCND_PFI_ATTENTION) == 0)
                            continue;
                        face = ccnd_face_from_faceid(h, p->faceid);
                        if (face->outstanding_interests == smallestq &&
                            ((p->pfi_flags & CCND_PFI_UPSTREAM) != 0)) {
                            if (best == 0) {
                                p->pfi_flags |= CCND_PFI_SENDUPST;
                                break;
                            }
                            best--;
                        }
                    }
                }
            }
            for (p = strategy->pfl; p!= NULL; p = p->next) {
                p->pfi_flags &= ~CCND_PFI_ATTENTION;
            }
            break;
        case CCNST_EXPUP:
            break;
        case CCNST_EXPDN:
            break;
        case CCNST_REFRESH:
            break;
        case CCNST_TIMER:
            break;
        case CCNST_SATISFIED:
            break;
        case CCNST_TIMEOUT: // all downstreams timed out, PIT entry will go away
            /* Interest has not been satisfied or refreshed */
            break;
        case CCNST_FINALIZE:
            /* Free the strategy per registration point private data */
            break;
    }
}
