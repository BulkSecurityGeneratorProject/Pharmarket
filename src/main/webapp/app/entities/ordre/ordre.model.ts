import {BaseEntity, User} from './../../shared';
import {Offre} from "../offre/offre.model";

export const enum OrderType {
    'DIRECT',
    'GROSSISTE'
}

export class Ordre implements BaseEntity {
    constructor(
        public id?: number,
        public totalPaid?: number,
        public totalOrdred?: number,
        public type?: OrderType,
        public status?: string,
        public paymentDueDate?: Date,
        public totalDiscount?: number,
        public customer?: BaseEntity,
        public orderHistories?: BaseEntity[],
        public orderDetails?: BaseEntity[],
        public offre?: Offre,
        public payment?: BaseEntity,
        public shipping?: BaseEntity,
        public shippingMode?: BaseEntity,
        public user?: User,
        public displayPaymentDueDate?: any,
        public createdBy?: any,
        public createdDate?: Date,
        public firstGrossiste?: BaseEntity,
        public secondGrossiste?: BaseEntity,
        public thirdGrossiste?: BaseEntity,


    ) {
    }
}
