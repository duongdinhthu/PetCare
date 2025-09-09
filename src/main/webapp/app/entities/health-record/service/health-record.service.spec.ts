import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IHealthRecord } from '../health-record.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../health-record.test-samples';

import { HealthRecordService, RestHealthRecord } from './health-record.service';

const requireRestSample: RestHealthRecord = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('HealthRecord Service', () => {
  let service: HealthRecordService;
  let httpMock: HttpTestingController;
  let expectedResult: IHealthRecord | IHealthRecord[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(HealthRecordService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a HealthRecord', () => {
      const healthRecord = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(healthRecord).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a HealthRecord', () => {
      const healthRecord = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(healthRecord).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a HealthRecord', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of HealthRecord', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a HealthRecord', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addHealthRecordToCollectionIfMissing', () => {
      it('should add a HealthRecord to an empty array', () => {
        const healthRecord: IHealthRecord = sampleWithRequiredData;
        expectedResult = service.addHealthRecordToCollectionIfMissing([], healthRecord);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(healthRecord);
      });

      it('should not add a HealthRecord to an array that contains it', () => {
        const healthRecord: IHealthRecord = sampleWithRequiredData;
        const healthRecordCollection: IHealthRecord[] = [
          {
            ...healthRecord,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHealthRecordToCollectionIfMissing(healthRecordCollection, healthRecord);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a HealthRecord to an array that doesn't contain it", () => {
        const healthRecord: IHealthRecord = sampleWithRequiredData;
        const healthRecordCollection: IHealthRecord[] = [sampleWithPartialData];
        expectedResult = service.addHealthRecordToCollectionIfMissing(healthRecordCollection, healthRecord);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(healthRecord);
      });

      it('should add only unique HealthRecord to an array', () => {
        const healthRecordArray: IHealthRecord[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const healthRecordCollection: IHealthRecord[] = [sampleWithRequiredData];
        expectedResult = service.addHealthRecordToCollectionIfMissing(healthRecordCollection, ...healthRecordArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const healthRecord: IHealthRecord = sampleWithRequiredData;
        const healthRecord2: IHealthRecord = sampleWithPartialData;
        expectedResult = service.addHealthRecordToCollectionIfMissing([], healthRecord, healthRecord2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(healthRecord);
        expect(expectedResult).toContain(healthRecord2);
      });

      it('should accept null and undefined values', () => {
        const healthRecord: IHealthRecord = sampleWithRequiredData;
        expectedResult = service.addHealthRecordToCollectionIfMissing([], null, healthRecord, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(healthRecord);
      });

      it('should return initial array if no HealthRecord is added', () => {
        const healthRecordCollection: IHealthRecord[] = [sampleWithRequiredData];
        expectedResult = service.addHealthRecordToCollectionIfMissing(healthRecordCollection, undefined, null);
        expect(expectedResult).toEqual(healthRecordCollection);
      });
    });

    describe('compareHealthRecord', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHealthRecord(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27332 };
        const entity2 = null;

        const compareResult1 = service.compareHealthRecord(entity1, entity2);
        const compareResult2 = service.compareHealthRecord(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27332 };
        const entity2 = { id: 20174 };

        const compareResult1 = service.compareHealthRecord(entity1, entity2);
        const compareResult2 = service.compareHealthRecord(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 27332 };
        const entity2 = { id: 27332 };

        const compareResult1 = service.compareHealthRecord(entity1, entity2);
        const compareResult2 = service.compareHealthRecord(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
