import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../health-record.test-samples';

import { HealthRecordFormService } from './health-record-form.service';

describe('HealthRecord Form Service', () => {
  let service: HealthRecordFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HealthRecordFormService);
  });

  describe('Service methods', () => {
    describe('createHealthRecordFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHealthRecordFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            petId: expect.any(Object),
            vetId: expect.any(Object),
            apptId: expect.any(Object),
            diagnosis: expect.any(Object),
            treatment: expect.any(Object),
            notes: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });

      it('passing IHealthRecord should create a new form with FormGroup', () => {
        const formGroup = service.createHealthRecordFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            petId: expect.any(Object),
            vetId: expect.any(Object),
            apptId: expect.any(Object),
            diagnosis: expect.any(Object),
            treatment: expect.any(Object),
            notes: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getHealthRecord', () => {
      it('should return NewHealthRecord for default HealthRecord initial value', () => {
        const formGroup = service.createHealthRecordFormGroup(sampleWithNewData);

        const healthRecord = service.getHealthRecord(formGroup) as any;

        expect(healthRecord).toMatchObject(sampleWithNewData);
      });

      it('should return NewHealthRecord for empty HealthRecord initial value', () => {
        const formGroup = service.createHealthRecordFormGroup();

        const healthRecord = service.getHealthRecord(formGroup) as any;

        expect(healthRecord).toMatchObject({});
      });

      it('should return IHealthRecord', () => {
        const formGroup = service.createHealthRecordFormGroup(sampleWithRequiredData);

        const healthRecord = service.getHealthRecord(formGroup) as any;

        expect(healthRecord).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHealthRecord should not enable id FormControl', () => {
        const formGroup = service.createHealthRecordFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHealthRecord should disable id FormControl', () => {
        const formGroup = service.createHealthRecordFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
