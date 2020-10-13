  /**
     * 엑셀 파일 업로드 모듈 
     * 
     */
    @RequestMapping(value = "/seat/ExcelFileUpload",method = RequestMethod.POST)
    public ResponseEntity<?> upload(MultipartFile uploadfile){
    
        fileUpload fupload = new fileUpload();
        String result = fupload.ExcelSaveFile(uploadfile);

        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        
        //업로드 실패 
        if(result == null ){
            resultMap.put("flag", "N");
            resultMap.put("msg", "업로드에 실패하였습니다.");
        }else{
            //업로드 성공
            resultMap.put("flag", "Y");
        resultMap.put("msg", "업로드에 성공하였습니다.");
        }
       
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    
    }
