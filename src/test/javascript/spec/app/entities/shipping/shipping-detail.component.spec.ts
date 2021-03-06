/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { PharmarketTestModule } from '../../../test.module';
import { ShippingDetailComponent } from '../../../../../../main/webapp/app/entities/shipping/shipping-detail.component';
import { ShippingService } from '../../../../../../main/webapp/app/entities/shipping/shipping.service';
import { Shipping } from '../../../../../../main/webapp/app/entities/shipping/shipping.model';

describe('Component Tests', () => {

    describe('Shipping Management Detail Component', () => {
        let comp: ShippingDetailComponent;
        let fixture: ComponentFixture<ShippingDetailComponent>;
        let service: ShippingService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [PharmarketTestModule],
                declarations: [ShippingDetailComponent],
                providers: [
                    ShippingService
                ]
            })
            .overrideTemplate(ShippingDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ShippingDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ShippingService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Shipping(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.shipping).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
