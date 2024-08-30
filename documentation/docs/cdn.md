# CDN Setup

## Configure Your Provider

The provider used for this project is: [bunny.net](https://bunny.net/)

- You need to create your own account — it's free for up to 4 weeks.
- You can choose any other provider if you prefer.
- Once you have an account, configure it and upload the images from the `data` subfolder.
- You can upload them manually or by using a tool like cURL.

### Use the Default Options While Creating the Setup
1. Add a storage zone:
    - The project uses `e-shop-bootcamp`. You can use your own.
2. Add a pull zone:
    - The project uses `e-shop-bootcamp`. Link it with the storage zone above.
3. Images are available at:  
    - `http://bootcamp.b-cdn.net/<storage-zone>/<file_name>.<file_extension>`

## Example Request to Bunny CDN to Upload `computer.png` Image

Replace <...> with the actual data.

```
curl --request PUT \
--url https://storage.bunnycdn.com/e-shop-bootcamp/<file_name>.<file_extension> \
--header 'AccessKey: <Find the key in the account settings -> FTP & API Access / Password>' \
--header 'Content-Type: application/octet-stream' \
--header 'accept: application/json' \ 
--data-binary @computer.png
```