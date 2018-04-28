/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { ConvertXTestModule } from '../../../test.module';
import { VideosDetailComponent } from '../../../../../../main/webapp/app/entities/videos/videos-detail.component';
import { VideosService } from '../../../../../../main/webapp/app/entities/videos/videos.service';
import { Videos } from '../../../../../../main/webapp/app/entities/videos/videos.model';

describe('Component Tests', () => {

    describe('Videos Management Detail Component', () => {
        let comp: VideosDetailComponent;
        let fixture: ComponentFixture<VideosDetailComponent>;
        let service: VideosService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [ConvertXTestModule],
                declarations: [VideosDetailComponent],
                providers: [
                    VideosService
                ]
            })
            .overrideTemplate(VideosDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(VideosDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(VideosService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Videos(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.videos).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
