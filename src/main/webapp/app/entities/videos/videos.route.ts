import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { VideosComponent } from './videos.component';
import { VideosDetailComponent } from './videos-detail.component';
import { VideosPopupComponent } from './videos-dialog.component';
import { VideosDeletePopupComponent } from './videos-delete-dialog.component';

@Injectable()
export class VideosResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
      };
    }
}

export const videosRoute: Routes = [
    {
        path: 'videos',
        component: VideosComponent,
        resolve: {
            'pagingParams': VideosResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'convertXApp.videos.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'videos/:id',
        component: VideosDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'convertXApp.videos.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const videosPopupRoute: Routes = [
    {
        path: 'videos-new',
        component: VideosPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'convertXApp.videos.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'videos/:id/edit',
        component: VideosPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'convertXApp.videos.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'videos/:id/delete',
        component: VideosDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'convertXApp.videos.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
