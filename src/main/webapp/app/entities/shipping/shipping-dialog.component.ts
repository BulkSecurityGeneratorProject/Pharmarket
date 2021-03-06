import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Shipping } from './shipping.model';
import { ShippingPopupService } from './shipping-popup.service';
import { ShippingService } from './shipping.service';
import { Offre, OffreService } from '../offre';

@Component({
    selector: 'jhi-shipping-dialog',
    templateUrl: './shipping-dialog.component.html'
})
export class ShippingDialogComponent implements OnInit {

    shipping: Shipping;
    isSaving: boolean;

    offres: Offre[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private shippingService: ShippingService,
        private offreService: OffreService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.offreService.query()
            .subscribe((res: HttpResponse<Offre[]>) => { this.offres = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.shipping.id !== undefined) {
            this.subscribeToSaveResponse(
                this.shippingService.update(this.shipping));
        } else {
            this.subscribeToSaveResponse(
                this.shippingService.create(this.shipping));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Shipping>>) {
        result.subscribe((res: HttpResponse<Shipping>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Shipping) {
        this.eventManager.broadcast({ name: 'shippingListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackOffreById(index: number, item: Offre) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}

@Component({
    selector: 'jhi-shipping-popup',
    template: ''
})
export class ShippingPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private shippingPopupService: ShippingPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.shippingPopupService
                    .open(ShippingDialogComponent as Component, params['id']);
            } else {
                this.shippingPopupService
                    .open(ShippingDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
