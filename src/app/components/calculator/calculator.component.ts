import {Component, Inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {CalculatorService} from "../services/calculator.service";

interface Champion {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-calculator',
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.scss']
})
export class CalculatorComponent implements OnInit {
  sort: string;
  champions: Champion[] = [
    {value: 'shaco', viewValue: 'Incel-Shaco'},
    {value: 'garen', viewValue: 'Garen'},
    {value: 'jhin', viewValue: 'Vir-Jhin'},
    {value: 'vi', viewValue: 'Vi-rgin'},
    {value: 'lux', viewValue: 'Lux'},
    {value: 'shen', viewValue: 'Xpetu Shen'},
  ];

  calculator: FormGroup = this.initForm();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private buildingService: CalculatorService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit() {
    this.calculator = this.initForm()
  }

  initForm() {
    return this.formBuilder.group({
      sex: new FormControl('',
        [Validators.required]
      ),
      weight: new FormControl('',
        [Validators.required, Validators.pattern("^[0-9]*$"), Validators.maxLength(10)]
      ),
      champion: new FormControl('', [Validators.required]),
    });
  }

  hasError(path: string, errorCode: string) {
    return this.calculator && this.calculator.hasError(errorCode, path);
  }

  navigateToBuildingsList() {
    this.router.navigate(['buildings']).then();
  }

  clickMethod() {

    confirm("Sa oled incel")
    // if(confirm("Are you sure to delete "+name)) {
    //   console.log("Implement delete functionality here");
    // }
  }

  submit() {
    const buildingToSave = { ...this.calculator.value};
    // this.dialog.open(DialogComponent)
    this.clickMethod();
    this.buildingService.post(buildingToSave).subscribe(
      result => {
      },
      error => {
      },
      () => {
        this.navigateToBuildingsList();
      }
    );
  }
}
