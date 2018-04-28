import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Videos } from './videos.model';
import { VideosPopupService } from './videos-popup.service';
import { VideosService } from './videos.service';

@Component({
    selector: 'jhi-videos-delete-dialog',
    templateUrl: './videos-delete-dialog.component.html'
})
export class VideosDeleteDialogComponent {

    videos: Videos;

    constructor(
        private videosService: VideosService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.videosService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'videosListModification',
                content: 'Deleted an videos'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-videos-delete-popup',
    template: ''
})
export class VideosDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private videosPopupService: VideosPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.videosPopupService
                .open(VideosDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
