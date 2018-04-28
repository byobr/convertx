import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { JhiDateUtils } from 'ng-jhipster';

import { Videos } from './videos.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<Videos>;

@Injectable()
export class VideosService {

    private resourceUrl =  SERVER_API_URL + 'api/videos';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(videos: Videos): Observable<EntityResponseType> {
        const copy = this.convert(videos);
        return this.http.post<Videos>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(videos: Videos): Observable<EntityResponseType> {
        const copy = this.convert(videos);
        return this.http.put<Videos>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Videos>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Videos[]>> {
        const options = createRequestOption(req);
        return this.http.get<Videos[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Videos[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Videos = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Videos[]>): HttpResponse<Videos[]> {
        const jsonResponse: Videos[] = res.body;
        const body: Videos[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Videos.
     */
    private convertItemFromServer(videos: Videos): Videos {
        const copy: Videos = Object.assign({}, videos);
        copy.dataEnviado = this.dateUtils
            .convertDateTimeFromServer(videos.dataEnviado);
        return copy;
    }

    /**
     * Convert a Videos to a JSON which can be sent to the server.
     */
    private convert(videos: Videos): Videos {
        const copy: Videos = Object.assign({}, videos);

        copy.dataEnviado = this.dateUtils.toDate(videos.dataEnviado);
        return copy;
    }
}
