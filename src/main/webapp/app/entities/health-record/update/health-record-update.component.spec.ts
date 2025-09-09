import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { HealthRecordService } from '../service/health-record.service';
import { IHealthRecord } from '../health-record.model';
import { HealthRecordFormService } from './health-record-form.service';

import { HealthRecordUpdateComponent } from './health-record-update.component';

describe('HealthRecord Management Update Component', () => {
  let comp: HealthRecordUpdateComponent;
  let fixture: ComponentFixture<HealthRecordUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let healthRecordFormService: HealthRecordFormService;
  let healthRecordService: HealthRecordService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HealthRecordUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(HealthRecordUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HealthRecordUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    healthRecordFormService = TestBed.inject(HealthRecordFormService);
    healthRecordService = TestBed.inject(HealthRecordService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const healthRecord: IHealthRecord = { id: 20174 };

      activatedRoute.data = of({ healthRecord });
      comp.ngOnInit();

      expect(comp.healthRecord).toEqual(healthRecord);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHealthRecord>>();
      const healthRecord = { id: 27332 };
      jest.spyOn(healthRecordFormService, 'getHealthRecord').mockReturnValue(healthRecord);
      jest.spyOn(healthRecordService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ healthRecord });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: healthRecord }));
      saveSubject.complete();

      // THEN
      expect(healthRecordFormService.getHealthRecord).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(healthRecordService.update).toHaveBeenCalledWith(expect.objectContaining(healthRecord));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHealthRecord>>();
      const healthRecord = { id: 27332 };
      jest.spyOn(healthRecordFormService, 'getHealthRecord').mockReturnValue({ id: null });
      jest.spyOn(healthRecordService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ healthRecord: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: healthRecord }));
      saveSubject.complete();

      // THEN
      expect(healthRecordFormService.getHealthRecord).toHaveBeenCalled();
      expect(healthRecordService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHealthRecord>>();
      const healthRecord = { id: 27332 };
      jest.spyOn(healthRecordService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ healthRecord });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(healthRecordService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
