import { Component, OnInit } from '@angular/core';
import {AuthService} from "../services/auth.service";
import {StoreService} from "../services/store.service";
import {Post} from "../model/Post";
import {HttpClient} from "@angular/common/http";
import {Product} from "../model/Product";
import {Router} from "@angular/router";

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.css']
})
export class StoreComponent implements OnInit {

  products: Product[] = [];

  constructor(
    private router: Router,
    private storeService: StoreService,
    private httpClient: HttpClient,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.storeService.getAll().subscribe(
      next => {
        // if (posts) {
        //   hideloader();
        // }
        console.log(next)
        // console.log(next)
        this.products = next
      }
      );
  }

  selectedFile: File;
  retrievedImage: any;
  base64Data: any;
  retrieveResonse: any;
  message: string;
  imageName: any;

  private apiUrl = '/api/store';

  create() {
    this.router.navigate(["store/new"])
  }

  //Gets called when the user selects an image
  public onFileChanged(event: any) {
    //Select File
    this.selectedFile = event.target.files[0];
  }


  //Gets called when the user clicks on submit to upload the image
  onUpload() {
    const product = new Product();
    product.id = 1
    // product.file = this.selectedFile;
    const uploadImageData = new FormData();
    uploadImageData.append('file', this.selectedFile, this.selectedFile.name);

    // @ts-ignore
    uploadImageData.append('title', "test");
    uploadImageData.append('desc', "desc");
    // @ts-ignore
    uploadImageData.append('cost', 7);

    // product.multipartFile = uploadImageData
    this.storeService.upload(uploadImageData).subscribe(
      res => {
        console.log(res);
      }
    );
  }

  image(array: any) {
    return  'data:image/jpeg;base64,' + array;
  }

  //Gets called when the user clicks on retieve image button to get the image from back end
  getImage() {
    //Make a call to Sprinf Boot to get the Image Bytes.
    this.httpClient.get(this.apiUrl + "/get/")
      .subscribe(
        res => {
          this.retrieveResonse = res;
          this.base64Data = this.retrieveResonse.picByte;
          this.retrievedImage = 'data:image/jpeg;base64,' + this.base64Data;
        }
      );
  }

}
