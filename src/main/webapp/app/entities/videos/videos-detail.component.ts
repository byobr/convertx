import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { Videos } from './videos.model';
import { VideosService } from './videos.service';

@Component({
    selector: 'jhi-videos-detail',
    templateUrl: './videos-detail.component.html'
})
export class VideosDetailComponent implements OnInit, OnDestroy {

    videos: Videos;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private videosService: VideosService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInVideos();
    }

    load(id) {
        this.videosService.find(id)
            .subscribe((videosResponse: HttpResponse<Videos>) => {
                this.videos = videosResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInVideos() {
        this.eventSubscriber = this.eventManager.subscribe(
            'videosListModification',
            (response) => this.load(this.videos.id)
        );
    }
}
