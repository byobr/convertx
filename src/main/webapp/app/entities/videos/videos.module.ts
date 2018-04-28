import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ConvertXSharedModule } from '../../shared';
import {
    VideosService,
    VideosPopupService,
    VideosComponent,
    VideosDetailComponent,
    VideosDialogComponent,
    VideosPopupComponent,
    VideosDeletePopupComponent,
    VideosDeleteDialogComponent,
    videosRoute,
    videosPopupRoute,
    VideosResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...videosRoute,
    ...videosPopupRoute,
];

@NgModule({
    imports: [
        ConvertXSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        VideosComponent,
        VideosDetailComponent,
        VideosDialogComponent,
        VideosDeleteDialogComponent,
        VideosPopupComponent,
        VideosDeletePopupComponent,
    ],
    entryComponents: [
        VideosComponent,
        VideosDialogComponent,
        VideosPopupComponent,
        VideosDeleteDialogComponent,
        VideosDeletePopupComponent,
    ],
    providers: [
        VideosService,
        VideosPopupService,
        VideosResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ConvertXVideosModule {}
