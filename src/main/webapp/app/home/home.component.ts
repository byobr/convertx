import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../app.constants';

import { Account, LoginModalService, Principal } from '../shared';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: [
        'home.css'
    ]

})
export class HomeComponent implements OnInit {
    account: Account;
    arquivo: any;
    modalRef: NgbModalRef;
    private resourceUrl =  SERVER_API_URL + 'api/videos';

    constructor(
        private principal: Principal,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private http: HttpClient
    ) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.principal.identity().then((account) => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    onSubmit() {
        console.log('aff');
        let formdata: FormData = new FormData();
        formdata.append('file', this.arquivo);
        formdata.append('user', this.account.login);
        this.http.post(this.resourceUrl + '/adicionar', formdata).subscribe(s => 
            this.enviouVideo()
        );
    }

    enviouVideo() {
        alert('Vídeo enviado com sucesso!');
        this.arquivo = null;
    }
}
