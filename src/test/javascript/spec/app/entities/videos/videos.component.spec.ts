/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { ConvertXTestModule } from '../../../test.module';
import { VideosComponent } from '../../../../../../main/webapp/app/entities/videos/videos.component';
import { VideosService } from '../../../../../../main/webapp/app/entities/videos/videos.service';
import { Videos } from '../../../../../../main/webapp/app/entities/videos/videos.model';

describe('Component Tests', () => {

    describe('Videos Management Component', () => {
        let comp: VideosComponent;
        let fixture: ComponentFixture<VideosComponent>;
        let service: VideosService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [ConvertXTestModule],
                declarations: [VideosComponent],
                providers: [
                    VideosService
                ]
            })
            .overrideTemplate(VideosComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(VideosComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(VideosService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Videos(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.videos[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
