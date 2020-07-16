/* 비즈니스 데이 구하기*/


    /**
     * 비즈니스 데이 날짜 계산
     * @param year
     * @param month
     * @param day
     * @param addDate
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/calenderCal", produces="text/plain")
    public String calenderCal(Locale locale, Model model, @RequestParam Map<String, Object> map  ) throws Exception{
		try {
			UserDetail userDetail = (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		}catch(Exception e) {
			throw new UnauthorizedException("로그인이 필요합니다.");
        }
        String month = map.get("month").toString();
        String day = map.get("day").toString();
        int addDate = Integer.parseInt( map.get("addDate").toString());
        logger.debug("year", map.get("year").toString());
        logger.debug("month", map.get("month").toString());
        logger.debug("day", map.get("day").toString());
        logger.debug("addDate", map.get("addDate").toString());

        //날짜와 월 : 2자리 만들어주기
        if(month.length() == 1) month = "0" + month;
        if(day.length() == 1) day = "0" + day;

        //addDate로 날짜 더하는 부분
        String calDate = map.get("year").toString() + month + day;
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = df.parse(calDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, addDate);

        calDate = df.format(cal.getTime());

		//        long date = DateUtils.parseDate("20180507", "yyyyMMdd").getTime();
        long dates = DateUtils.parseDate(calDate, "yyyyMMdd").getTime();
//        long date = DateUtils.parseDate("20171006", "yyyyMMdd").getTime();
        boolean isLegalHoliday = isLegalHoliday(dates);
        boolean isWeekend = isWeekend(dates);
        boolean isAlternative = isAlternative(dates);
        boolean isHoliday = isHoliday(dates);
        //true로 나오면 휴일
        //false로 나오면 휴일아님
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isLegalHoliday", isLegalHoliday);

        JSONObject json = new JSONObject();
        json.put("isLegalHoliday", isLegalHoliday);
        json.put("isWeekend", isWeekend);
        json.put("isAlternative", isAlternative);
        json.put("isHoliday", isHoliday);
        json.put("calDate", calDate);

		return json.toJSONString();
    }


    /**
     * 공휴일 여부
     * @param date
     */
    public static boolean isHoliday(long date) {
        return isLegalHoliday(date) || isWeekend(date) || isAlternative(date);
    }

    /**
     * 음력날짜 구하기
     * @param date
     * @return
     */
    public static String getLunarDate(long date) {
        ChineseCalendar cc = new ChineseCalendar(new java.util.Date(date));
        String m = String.valueOf(cc.get(ChineseCalendar.MONTH) + 1);
        m = StringUtils.leftPad(m, 2, "0");
        String d = String.valueOf(cc.get(ChineseCalendar.DAY_OF_MONTH));
        d = StringUtils.leftPad(d, 2, "0");

        return m + d;
    }

    /**
     * 법정휴일
     * @param date
     * @return
     */
    public static boolean isLegalHoliday(long date) {
        boolean result = false;

        String[] solar = {"0101", "0301", "0505", "0606", "0815", "1225"};
        String[] lunar = {"0101", "0102", "0408", "0814", "0815", "0816", "1231"};

        List<String> solarList = Arrays.asList(solar);
        List<String> lunarList = Arrays.asList(lunar);

        String solarDate = DateFormatUtils.format(date, "MMdd");
        ChineseCalendar cc = new ChineseCalendar(new java.util.Date(date));

//        String y = String.valueOf(cc.get(ChineseCalendar.EXTENDED_YEAR) - 2637);
        String m = String.valueOf(cc.get(ChineseCalendar.MONTH) + 1);
        m = StringUtils.leftPad(m, 2, "0");
        String d = String.valueOf(cc.get(ChineseCalendar.DAY_OF_MONTH));
        d = StringUtils.leftPad(d, 2, "0");

        String lunarDate = m + d;

        if (solarList.indexOf(solarDate) >= 0) {
            return true;
        }
        if (lunarList.indexOf(lunarDate) >= 0) {
            return true;
        }

        return result;
    }

    /**
     * 주말 (토,일)
     * @param date
     * @return
     */
    public static boolean isWeekend(long date) {
        boolean result = false;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        //SUNDAY:1 SATURDAY:7
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            result = true;
        }

        return result;
    }

    /**
     * 대체공휴일
     * @param date
     * @return
     */
    public static boolean isAlternative(long date) {
        boolean result = false;

        //설날 연휴와 추석 연휴가 다른 공휴일과 겹치는 경우 그 날 다음의 첫 번째 비공휴일을 공휴일로 하고, 어린이날이 토요일 또는 다른 공휴일과 겹치는 경우 그 날 다음의 첫 번째 비공휴일을 공휴일로 함
        //1. 어린이날
        String year = DateFormatUtils.format(date, "yyyy");
        java.util.Date d = null;
        try {
            d = DateUtils.parseDate(year+"0505", "yyyyMMdd");
        } catch (ParseException e) {}
        if (isWeekend(d.getTime()) == true) {
            d = DateUtils.addDays(d, 1);
        }
        if (isWeekend(d.getTime()) == true) {
            d = DateUtils.addDays(d, 1);
        }
        if (DateUtils.isSameDay(new java.util.Date(date), d) == true) {
            result = true;
        }

        //2. 설
        String lunarDate = getLunarDate(date);
        Calendar calendar = Calendar.getInstance();
        d = new java.util.Date(date);
        if (StringUtils.equals(lunarDate, "0103")) {

            d = DateUtils.addDays(d, -1);
            calendar.setTime(d);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return true;
            }

            d = DateUtils.addDays(d, -1);
            calendar.setTime(d);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return true;
            }

            d = DateUtils.addDays(d, -1);
            calendar.setTime(d);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return true;
            }
        }

        //3. 추석
        d = new java.util.Date(date);
        if (StringUtils.equals(lunarDate, "0817")) {
            d = DateUtils.addDays(d, -1);
            calendar.setTime(d);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return true;
            }

            d = DateUtils.addDays(d, -1);
            calendar.setTime(d);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return true;
            }

            d = DateUtils.addDays(d, -1);
            calendar.setTime(d);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return true;
            }
        }

        return result;
    }



    /* gradle 더하기 */
    // https://mvnrepository.com/artifact/com.ibm.icu/icu4j
compile group: 'com.ibm.icu', name: 'icu4j', version: '59.1'
