FROM nginx:1.17.1-alpine
RUN rm /etc/nginx/conf.d/default.conf
COPY /dist/ubemed /usr/share/nginx/html
COPY /conf /etc/nginx/conf.d
