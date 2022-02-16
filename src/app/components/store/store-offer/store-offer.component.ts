import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-store-offer',
  templateUrl: './store-offer.component.html',
  styleUrls: ['./store-offer.component.css']
})
export class StoreOfferComponent implements OnInit {

  id: number
  cost: number;
  form: FormGroup;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {id: number, cost: number},
    private formBuilder: FormBuilder,
    private dialogref: MatDialogRef<StoreOfferComponent>
  ) {
    this.id = data.id;
    this.cost = data.cost;
    this.form = this.initForm();
  }

  ngOnInit(): void {
  }

  initForm() {
    return this.formBuilder.group({
      offer: new FormControl('',
        [Validators.required, Validators.min(this.cost + 1)]
      ),
    });
  }

  hasError(path: string, errorCode: string) {
    return this.form && this.form.hasError(errorCode, path);
  }

  submit() {
    this.dialogref.close();
  }

}
