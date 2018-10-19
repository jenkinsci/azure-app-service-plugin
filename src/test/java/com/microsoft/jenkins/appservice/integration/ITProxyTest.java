package com.microsoft.jenkins.appservice.integration;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.util.AzureCredentials;
import com.microsoft.jenkins.appservice.util.AzureUtils;
import com.microsoft.jenkins.azurecommons.core.credentials.TokenCredentialData;
import hudson.ProxyConfiguration;
import hudson.model.Item;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Queue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AzureUtils.class)
public class ITProxyTest extends IntegrationTest {
    private static HttpProxyServer proxyServer;

    private static final int PROXY_SERVER_PORT = 33333;
    private static final int WEB_SERVER_PORT = 44444;
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final String WEB_SERVER_MSG = "web server";
    private static final String PROXY_SERVER_MSG = "proxy";

//    protected static class BaseChainedProxy extends ChainedProxyAdapter {
//        String username;
//        String password;
//        String host;
//        Integer port;
//
//        public BaseChainedProxy(String host, Integer port, String username, String password) {
//            this.host = host;
//            this.port = port;
//            this.username = username;
//            this.password = password;
//        }
//
//        @Override
//        public InetSocketAddress getChainedProxyAddress() {
//            return new InetSocketAddress(this.host, this.port);
//        }
//
//        @Override
//        public void filterRequest(HttpObject httpObject) {
//            System.out.println("filter");
//            if (httpObject instanceof FullHttpRequest && this.username != null) {
////                ((FullHttpRequest) httpObject).headers().add("Proxy-Authorization", "Basic " + CrawlFactoryService.generateUserAndPasswordBase64(this.username, this.password));
//            }
//        }
//    }


//    @BeforeClass
    public static void launchServers() {
//        launchWebServer();
        launchProxyServer();
    }

    private static void launchProxyServer() {
        proxyServer = DefaultHttpProxyServer.bootstrap()
                .withPort(PROXY_SERVER_PORT)
                .withName("proxy")
                .withChainProxyManager(new ChainedProxyManager() {
                    @Override
                    public void lookupChainedProxies(final HttpRequest httpRequest, Queue<ChainedProxy> chainedProxies) {
//                        ChainedProxy s = new BaseChainedProxy("127.0.0.1", WEB_SERVER_PORT, "", "");
                        ChainedProxy proxy = new ChainedProxyAdapter() {
                            @Override
                            public InetSocketAddress getChainedProxyAddress() {
//                                return new InetSocketAddress("0.0.0.0", WEB_SERVER_PORT);
                                try {
                                    URL url = new URL(httpRequest.uri());
                                    return new InetSocketAddress(InetAddress.getByName(url.getHost()), 80);
                                } catch (Exception e) {
                                    throw new RuntimeException();
                                }
                            }
                        };
                        chainedProxies.add(proxy);

//                        chainedProxies.add(ChainedProxyAdapter.FALLBACK_TO_DIRECT_CONNECTION);

                    }
                })
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest) {
                        return new HttpFiltersAdapter(originalRequest) {
                            @Override
                            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
//                                if(httpObject instanceof HttpRequest){
//                                    HttpRequest request = (HttpRequest) httpObject;
//                                    HttpHeaders headers = request.headers();
//                                    headers.remove(HttpHeaders.Names.IF_MODIFIED_SINCE);
//                                }
                                return null;
//                                HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.NOT_FOUND,
//                                        Unpooled.wrappedBuffer(PROXY_SERVER_MSG.getBytes()));
//                                return response;
                            }

                            @Override
                            public HttpResponse proxyToServerRequest(HttpObject httpObject) {
                                return super.proxyToServerRequest(httpObject);
                            }

                            @Override
                            public HttpObject serverToProxyResponse(HttpObject httpObject) {
                                return super.serverToProxyResponse(httpObject);
                            }
                        };
                    }
                })
                .start();
    }

//    @AfterClass
    public static void shutdownLocalProxy() {
        if (proxyServer != null) {
            proxyServer.stop();
        }
    }

    @Test
    public void test() {
        j.jenkins.proxy = new ProxyConfiguration(LOCAL_ADDRESS, PROXY_SERVER_PORT);


        AzureCredentials azureCredentials = new AzureCredentials(CredentialsScope.GLOBAL, "test",
                "test", testEnv.subscriptionId, testEnv.clientId, testEnv.clientSecret);
        TokenCredentialData token = azureCredentials.createToken();
        PowerMockito.mockStatic(AzureUtils.class);
//        BDDMockito.given(AzureUtils.getToken(Mockito.any(Item.class), Mockito.anyString())).willReturn(token);
        PowerMockito.when(AzureUtils.getToken(Mockito.any(Item.class), Mockito.anyString())).thenReturn(token);
        TokenCredentialData token1 = AzureUtils.getToken(null, "");
        System.out.println(token1.toString());

//        Azure azure = AzureUtils.buildClient(null, "");
//
//
//        int size = azure.resourceGroups().list().size();
//        System.out.println(size);
    }

}

