import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Videos } from './videos.model';
import { VideosPopupService } from './videos-popup.service';
import { VideosService } from './videos.service';

@Component({
    selector: 'jhi-videos-dialog',
    templateUrl: './videos-dialog.component.html'
})
export class VideosDialogComponent implements OnInit {

    videos: Videos;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private videosService: VideosService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.videos.id !== undefined) {
            this.subscribeToSaveResponse(
                this.videosService.update(this.videos));
        } else {
            this.subscribeToSaveResponse(
                this.videosService.create(this.videos));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Videos>>) {
        result.subscribe((res: HttpResponse<Videos>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Videos) {
        this.eventManager.broadcast({ name: 'videosListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-videos-popup',
    template: ''
})
export class VideosPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private videosPopupService: VideosPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.videosPopupService
                    .open(VideosDialogComponent as Component, params['id']);
            } else {
                this.videosPopupService
                    .open(VideosDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
